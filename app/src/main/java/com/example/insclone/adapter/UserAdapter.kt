package com.example.insclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.insclone.R
import com.example.insclone.fragmen.ProfileFragment
import com.example.insclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private var mContext: Context,
    private val mUser: List<User>,
    private var isFragment: Boolean = false): RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.txtUsernameSearch.text = user.getUsername()
        holder.txtFullnameSearch.text = user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.imgUserProfile)

        cekFollowingStatus(user.getUID(), holder.btnFollow)

        holder.itemView.setOnClickListener{
            val pref =mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.getUID())
            pref.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragmen_container, ProfileFragment()).commit()
        }

        holder.btnFollow.setOnClickListener{
            if (holder.btnFollow.text.toString() == "Follow"){
                firebaseUser?.uid.let { task ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(task.toString())
                        .child("Following").child(user.getUID())
                        .setValue(true).addOnCompleteListener{ task ->

                            if (task.isSuccessful){
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful){

                                            }
                                        }
                                }
                            }
                        }
                }
            }else{
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUID())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful){

                                        }
                                }
                        }
                            }
                        }
                }
            }
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var txtUsernameSearch: TextView = itemView.findViewById(R.id.user_name_serach)
        var txtFullnameSearch: TextView = itemView.findViewById(R.id.user_fullname_search)
        var imgUserProfile: CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        var btnFollow: Button = itemView.findViewById(R.id.btn_follow)

    }

    private fun cekFollowingStatus(uid: String, btnFollow: Button) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }
        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(uid).exists()){
                    btnFollow.text = "Following"
                }else{
                    btnFollow.text = "Follow"
                }
            }
        })
    }

}