package com.example.whatsnew.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsnew.Adapters.UserAdapter
import com.example.whatsnew.Models.ChatList
import com.example.whatsnew.Models.Users
import com.example.whatsnew.Notification.Token
import com.example.whatsnew.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId


class ChatFragment : Fragment() {

    private var userAdapter: UserAdapter?=null
    private var mUsers:List<Users> ?=null
    lateinit var recycler_view_chat_List: RecyclerView
    private var usersChatList:List<ChatList> ?=null
    private var firebaseUser:FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_chat, container, false)


        // Recycler view chat
        recycler_view_chat_List = view.findViewById(R.id.recycler_view_chat_List)
        recycler_view_chat_List.setHasFixedSize(true)
        recycler_view_chat_List.layoutManager = LinearLayoutManager(context)
        // ...........................

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatLists").child(firebaseUser!!.uid)

        ref!!.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                (usersChatList as ArrayList).clear()

                for(datasnapshot in snapshot.children)
                {
                    val chatlist = datasnapshot.getValue(ChatList::class.java)
                    (usersChatList as ArrayList).add(chatlist!!)

                }

                RetriveChatList()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        UpdateToken(FirebaseInstanceId.getInstance().token)

        return view
    }

    private fun UpdateToken(token: String?)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun RetriveChatList()
    {
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")

        ref!!.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                (mUsers as ArrayList).clear()

                for(datasnapshot in snapshot.children)
                {
                    val user = datasnapshot.getValue(Users::class.java)

                    for(eachChatList in usersChatList!!)
                    {
                        if(user!!.getUID().equals(eachChatList.getId()))
                        {
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }

                userAdapter = UserAdapter(context!!,(mUsers as ArrayList<Users>) , true)
                recycler_view_chat_List.adapter = userAdapter

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


}