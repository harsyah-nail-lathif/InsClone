package com.example.insclone.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.insclone.CommentActivity
import com.example.insclone.MainActivity
import com.example.insclone.R
import com.example.insclone.model.Post
import com.example.insclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.post_layout.view.*

class PostAdapter(private val mContext: Context, private val mPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView) {
        //you make a stupid mistake here
        var profileImaage:CircleImageView = itemView.findViewById(R.id.user_profile_image)
        var postImage:ImageView = itemView.findViewById(R.id.post_image_home)
        var likebutton:ImageView = itemView.findViewById(R.id.post_image_like_button)
        var commentButton:ImageView = itemView.findViewById(R.id.post_image_comment_button)
        var savebutton:ImageView = itemView.findViewById(R.id.post_image_save_button)
        var userName:TextView = itemView.findViewById(R.id.post_user_name)
        var likes:TextView = itemView.findViewById(R.id.post_likes)
        var publisher:TextView = itemView.findViewById(R.id.post_publisher)
        var description:TextView = itemView.findViewById(R.id.post_description)
        var comment:TextView = itemView.findViewById(R.id.post_comments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]
        Picasso.get().load(post.getPostimage()).into(holder.postImage)
        if(post.getDescription().equals("")){
            holder.description.visibility = View.GONE
        }else{
            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.getDescription())
        }
        //methode posting
        publisherInfo(holder.profileImaage, holder.userName, holder.publisher, post.getPublisher())
        //methode likes
        isLikes(post.getPostid(), holder.likebutton)
        //methode melihat jumlah likes
        numberOfLikes(holder.likes, post.getPostid())
        //methode meluaht jumlah comment
        getTotalComment(holder.comment, post.getPostid())

        holder.likebutton.setOnClickListener{
            if (holder.likebutton.tag == "Like"){
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(post.getPostid()).child(firebaseUser!!.uid)
                    .setValue(true)
            }else{
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(post.getPostid()).child(firebaseUser!!.uid)
                    .removeValue()

                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }
        holder.commentButton.setOnClickListener{
            val intentComment = Intent(mContext,CommentActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }

        holder.comment.setOnClickListener {
            val intentComment = Intent(mContext,CommentActivity::class.java)
            intentComment.putExtra("postId", post.getPostid())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }
    }

    private fun getTotalComment(comment: TextView, postid: String) {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postid)

        commentRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    comment.text = "view all" + p0.childrenCount.toString() + "comments"
                }
            }
        })
    }

    private fun numberOfLikes(likes: TextView, postid: String) {
        val likeRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)




        likeRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    likes.text = p0.childrenCount.toString() + "Likes"
                }
            }
        })
    }

    private fun isLikes(postid: String, likebutton: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likeRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postid)

        likeRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()){
                    likebutton.setImageResource(R.drawable.heart_clicked)
                    likebutton.tag = "Liked"
                }else{
                    likebutton.setImageResource(R.drawable.heart)
                    likebutton.tag = "Like"
                }
            }
        })
    }

    private fun publisherInfo(
        profileImaage: CircleImageView,
        userName: TextView,
        publisher: TextView,
        publisherID: String
    ) {
        val userRef = FirebaseDatabase.getInstance().reference.child("user").child(publisherID)
        userRef.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile)
                        .into(profileImaage)
                    userName.text = user?.getUsername()
                    publisher.text = user?.getFullname()
                }
            }
        })
    }
}