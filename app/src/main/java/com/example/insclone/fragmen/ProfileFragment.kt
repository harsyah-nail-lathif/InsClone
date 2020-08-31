package com.example.insclone.fragmen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.insclone.AccountSettingActivity
import com.example.insclone.R
import com.example.insclone.adapter.MyImageAdapter
import com.example.insclone.model.Post
import com.example.insclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    var postlistgrid: MutableList<Post>? = null
    var myImageAdapter: MyImageAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewprofile = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        var recyclerViewUdloadImage : RecyclerView? = null
        recyclerViewUdloadImage = viewprofile.findViewById(R.id.recycler_upload_pictiamge)
        recyclerViewUdloadImage.setHasFixedSize(true)
        var linearLayoutManager = GridLayoutManager(context, 3)
        recyclerViewUdloadImage?.layoutManager = linearLayoutManager

        postlistgrid = ArrayList()
        myImageAdapter = context?.let { MyImageAdapter(it, postlistgrid as ArrayList<Post>) }
        recyclerViewUdloadImage?.adapter = myImageAdapter



        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("profileId", "none")!!
        }
        if (profileId == firebaseUser.uid){
            view?.btn_edit_profile?.text = "Edit Profile"
        }else if(profileId != firebaseUser.uid){
            cekFollowAndFollowingbuttonStatus()
        }


        viewprofile.btn_edit_profile.setOnClickListener{
            val getButtonText = view?.btn_edit_profile?.text.toString()

            when{
                getButtonText == "Edit Profile" -> startActivity(Intent(context, AccountSettingActivity::class.java))
                getButtonText == "Follow" ->{
                    firebaseUser?.uid.let {
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Following").child(profileId).setValue(true)
                    }

                    firebaseUser?.uid.let {
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it.toString()).setValue(true)
                    }
                }
                getButtonText == "Following" ->{
                    firebaseUser?.uid.let {
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Following").child(profileId).removeValue()
                    }
                    firebaseUser?.uid.let {
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it.toString()).removeValue()
                    }
                }
            }
        }

        getFollowers()
        getFollowing()
        getInfo()
        myPost()
        return viewprofile
    }

    private fun myPost() {
        val postRef = FirebaseDatabase.getInstance().reference
            .child("Posts")
        postRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    (postlistgrid as ArrayList<Post>).clear()

                    for (snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)
                        if (post?.getPublisher().equals(profileId)){
                            (postlistgrid as ArrayList<Post>).add(post!!)
                        }
                        Collections.reverse(postlistgrid)
                        myImageAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun getInfo() {
        val userRef = FirebaseDatabase.getInstance().getReference()
            .child("user").child(profileId)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue<User>(User::class.java)

                Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile)
                    .into(view?.profile_image)
                view?.profile_fragment_username?.text = user?.getUsername()
                view?.txt_full_username?.text = user?.getFullname()
                view?.txt_full_bio?.text = user?.getBio()
            }
        })
    }

    private fun getFollowing() {
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    view?.total_following?.text = p0.childrenCount.toString()
                }
            }
        })
    }

    private fun getFollowers() {
        val followersref = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    view?.total_followers?.text = p0.childrenCount.toString()
                }
            }
        })
    }

    private fun cekFollowAndFollowingbuttonStatus() {
        val followingRef = firebaseUser?.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }
        if (followingRef != null){
            followingRef.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child(profileId).exists()){
                        view?.btn_edit_profile?.text = "Following"
                    }else{
                        view?.btn_edit_profile?.text = "Follow"
                    }
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

}