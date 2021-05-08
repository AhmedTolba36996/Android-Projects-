package com.example.whatsnew.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsnew.Adapters.UserAdapter
import com.example.whatsnew.Models.Users
import com.example.whatsnew.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment() {

    private var userAdapter:UserAdapter?=null
    private var mUsers:List<Users> ?=null
    private var recyclerView:RecyclerView ?=null
    private var searchEdittext:EditText ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recycler_serach)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchEdittext = view.findViewById(R.id.search_edit_text)

        mUsers = ArrayList()

        RetriveAllUsers()

        searchEdittext!!.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

              SearchForUser(s.toString().toLowerCase())

            }

        })

        return view
    }

    private fun RetriveAllUsers()
    {
         var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

         val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                (mUsers as ArrayList<Users>).clear()

                if(searchEdittext!!.text.toString() == "")
                {
                    for(datasnapshot in snapshot.children)
                    {
                        val user:Users?= datasnapshot.getValue(Users::class.java)
                        if(!(user!!.getUID()).equals(firebaseUserID))
                        {
                            (mUsers as ArrayList<Users>).add(user)
                        }
                    }

                    userAdapter = UserAdapter(context!! , mUsers!! , false)
                    recyclerView!!.adapter = userAdapter
                }


            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun SearchForUser(str:String)
    {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val queryUsers = FirebaseDatabase.getInstance().reference.child("Users")
                .orderByChild("search")
                .startAt(str)
                .endAt(str+"\ufaff")
        queryUsers.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                (mUsers as ArrayList<Users>).clear()
                for(datasnapshot in snapshot.children)
                {
                    val user:Users?= datasnapshot.getValue(Users::class.java)
                    if(!user!!.getUID().equals(firebaseUserID))
                    {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!! , mUsers!! , false)
                recyclerView!!.adapter = userAdapter


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}