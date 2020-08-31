package com.example.insclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_signin_link.setOnClickListener {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
    }
        btn_register.setOnClickListener{
            createAccount()
        }
    }

    private fun createAccount() {
        //untuk memberikan aksi pada edit text di laman register
        val fullName = fullname_register.text.toString()
        val username = username_register.text.toString()
        val email = email_register.text.toString()
        val password = password_register.toString()

        //memberikan peringatan saat kolom yang harus di isi kosong
        when{
            TextUtils.isEmpty(fullName)->Toast.makeText(this, "Full Name Required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(username)->Toast.makeText(this, "Username Required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(email)->Toast.makeText(this, "Email Required", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(password)->Toast.makeText(this, "Password Required", Toast.LENGTH_SHORT).show()

            else->{
             val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Register")
                progressDialog.setMessage("Please Wait")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                //aksi registreasi
                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener{task->
                        if (task.isSuccessful){
                            saveUserInfo(fullName, username, email, progressDialog)
                        }else{
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }

            }
        }

    }

    private fun saveUserInfo(
        fullName: String,
        username: String,
        email: String,
        //kode yang di coret maksudnya tidak di sarankan tapi bisa di gunakan
        progressDialog: ProgressDialog
    ) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        //membuat tabel baru secara realtime dengan kode 'child'
        /*child tidak mesti untuk membuat database. berdasarkan kode di depannnya. kode databse
         reference di gunakan untuk membuat database baru*/
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("user")
        val userMap = HashMap<String,Any>()
        userMap["uid"] = currentUserId
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = username.toLowerCase()
        userMap["email"] = email.toLowerCase()
        //default bio sebelum di edit
        userMap["bio"] = "hey I'm student of IDN Boarding School"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/socialmedia-688c0.appspot.com/o/Default%20Image%2Fprofile.png?alt=media&token=c5d37e97-c798-4cae-9ce0-3898924542cc"

        usersRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this@RegisterActivity, "Account sudah berhasil di buat", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    val message = task.exception!!.toString()
                    Toast.makeText(this@RegisterActivity, "Error: $message", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}