package com.example.insclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.insclone.adapter.CommentAdapter
import com.example.insclone.model.Comment
import com.example.insclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentAdapter: CommentAdapter? = null
    private var commentlist: MutableList<Comment>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        var recyclerView: RecyclerView? = null
        recyclerView = findViewById(R.id.recycler_comment)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentlist = ArrayList()
        commentAdapter = CommentAdapter(this, commentlist as ArrayList<Comment>)
        recyclerView.adapter = commentAdapter

        userInfo()
        readComment()
        getpostImage()

        txtpost_comment.setOnClickListener {
            if (add_comment!!.text.toString() == ""){
                Toast.makeText(this, "Please insert the comment", Toast.LENGTH_SHORT).show()
            }else{
                addCommnet()
            }
        }
    }

    private fun getpostImage() {
        val postCmmetRef = FirebaseDatabase.getInstance().reference
            .child("Posts").child(postId).child("postImage")

        postCmmetRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val image = p0.value.toString()

                    Picasso.get().load(image).placeholder(R.drawable.profile)
                        .into(post_image_comment)
                }
            }
        })
    }

    private fun readComment() {
        var commentRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postId)

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    commentlist!!.clear()
                    for (snapshot in p0.children){
                        val comment = snapshot.getValue(Comment::class.java)
                        commentlist!!.add(comment!!)
                    }
                    commentAdapter!!.notifyDataSetChanged()
                }
            }
        })
    }

    private fun addCommnet() {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postId!!)

        val commentmap = HashMap<String, Any>()
        commentmap["comment"] = add_comment!!.text.toString()
        commentmap["publisher"] = firebaseUser!!.uid

        commentRef.push().setValue(commentmap)

        add_comment!!.text.clear()
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("user").child(firebaseUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue<User>(User::class.java)


                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profile_image_comment)

                }
            }
        })
    }
}