package com.example.insclone.fragmen

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.insclone.R
import com.example.insclone.adapter.UserAdapter
import com.example.insclone.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var myUser: MutableList<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.search_recyclerview)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        myUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, myUser as ArrayList<User>, true) }
        recyclerView?.adapter = userAdapter

        view.edt_search.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (view.edt_search.toString() == ""){

                }else{
                    recyclerView?.visibility = View.VISIBLE
                    getUsers()
                    searchUSer(s.toString().toLowerCase())
                }
            }
        })
        return view
    }

    private fun searchUSer(input: String) {
        //untuk mengambil data dari database
        val query = FirebaseDatabase.getInstance().getReference()
            .child("user")
            .orderByChild("fullname")
            .startAt(input).endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                myUser?.clear()
                for (snapshoot in p0.children){
                    val user = snapshoot.getValue(User::class.java)
                    if (user!= null){
                        myUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }
        })
    }

    private fun getUsers() {
        val userRef = FirebaseDatabase.getInstance().getReference().child("user")
        userRef.addValueEventListener(object  : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (view?.edt_search?.toString() == ""){
//                    myUser?.clear()

                    for(snapshoot in p0.children){
                        val user = snapshoot.getValue(User::class.java)
                        if (user != null){
                            myUser?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()
                }
            }
        })
    }
}