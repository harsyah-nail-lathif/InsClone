package com.example.insclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.insclone.R
import com.example.insclone.model.Comment
import com.example.insclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val mContext:Context, private val mComment: MutableList<Comment>)
    : RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    private var firebaseUser:FirebaseUser? = null

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        //intsalasi id di comment layout
        var imageProfile: CircleImageView = itemView.findViewById(R.id.user_profile_image_comment)
        var usernameCommentTv: TextView = itemView.findViewById(R.id.username_comment)
        var commentTv: TextView = itemView.findViewById(R.id.comment_comment)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComment.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = mComment[position]

        holder.commentTv.text = comment.getComment()
        //create methode untuk ambil data user

        getUserInfo(holder.imageProfile, holder.usernameCommentTv, comment.getPublisher())

    }

    private fun getUserInfo(
        imageProfile: CircleImageView,
        usernameCommentTv: TextView,
        publisher: String
    ) {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("user").child(publisher)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user  = p0.getValue(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(imageProfile)
                    usernameCommentTv.text = user.getUsername()
                }
            }
        })
    }
}