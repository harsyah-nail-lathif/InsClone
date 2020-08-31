package com.example.insclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.insclone.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var firebaseUSer : FirebaseUser
    private var cekProfile = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageImagePictureRef: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        firebaseUSer = FirebaseAuth.getInstance().currentUser!!
        storageImagePictureRef = FirebaseStorage.getInstance().reference.child("Profile Picture")


        btn_logout_account.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingActivity, LoginActivity::class.java)
            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        change_setting_text.setOnClickListener{
            cekProfile = "clicked"

            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingActivity)

        }

        btn_info_profile.setOnClickListener{
            if (cekProfile == "clicked"){

                uploadImageAndUpdateInfo()
            }else{
                updateUserInfoOnly()
            }
        }
        userInfo()
    }

    private fun uploadImageAndUpdateInfo() {
        when{
            imageUri == null -> Toast.makeText(this, "Please Select The Image", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(fullname_setting.text.toString())->{
                Toast.makeText(this, "PLease Don't Leave This Empty", Toast.LENGTH_SHORT).show()
            }
            username_setting.text.toString() == "" -> {
                Toast.makeText(this, "PLease Don't Leave This Empty", Toast.LENGTH_SHORT).show()
            }
            bio_setting.text.toString() == "" ->{
                Toast.makeText(this, "PLease Don't Leave This Empty", Toast.LENGTH_SHORT).show()
            }
            else-> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Setting")
                progressDialog.setMessage("Uploading Data...")
                progressDialog.show()

                val fileRef = storageImagePictureRef!!.child(firebaseUSer!!.uid + "jpg")

                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception.let {
                            throw it!!
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("user")

                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = fullname_setting.text.toString().toLowerCase()
                        userMap["username"] = username_setting.text.toString().toLowerCase()
                        userMap["bio"] = bio_setting.text.toString().toLowerCase()
                        userMap["image"] = myUrl

                        ref.child(firebaseUSer.uid).updateChildren(userMap)

                        Toast.makeText(this, "Profile Berhasil di Update", Toast.LENGTH_SHORT)
                            .show()

                        val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK
            && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            setprofile_image_view.setImageURI(imageUri)

        }
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().getReference()
            .child("user").child(firebaseUSer.uid)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val users = p0.getValue<User>(User::class.java)

                    Picasso.get().load(users!!.getImage()).placeholder(R.drawable.profile)
                        .into(setprofile_image_view)
                    username_setting.setText(users.getUsername())
                    fullname_setting.setText(users.getFullname())
                    bio_setting.setText(users.getBio())
                }
            }
        })
    }

    private fun updateUserInfoOnly() {
        //else untuk when tidak ada di tutup when
        when{
            TextUtils.isEmpty(fullname_setting.text.toString())->{
                Toast.makeText(this, "PLease Don't Leave This Empty", Toast.LENGTH_SHORT).show()
            }
            username_setting.text.toString() == "" -> {
                Toast.makeText(this, "PLease Don't Leave This Empty", Toast.LENGTH_SHORT).show()
            }
            bio_setting.text.toString() == "" ->{
                Toast.makeText(this, "PLease Don't Leave This Empty", Toast.LENGTH_SHORT).show()
            }
            else ->{
                val userRef = FirebaseDatabase.getInstance().reference
                    .child("user")
                val userMap = HashMap<String, Any>()
                userMap["fullname"] = fullname_setting.text.toString().toLowerCase()
                userMap["username"] = username_setting.text.toString().toLowerCase()
                userMap["bio"] = bio_setting.text.toString().toLowerCase()

                userRef.child(firebaseUSer.uid).updateChildren(userMap)

                Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                startActivity(intent)
                finish()



            }
        }
    }
}