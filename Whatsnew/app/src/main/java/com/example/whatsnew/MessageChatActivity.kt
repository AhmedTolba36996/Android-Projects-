package com.example.whatsnew

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsnew.Adapters.ChatAdapter
import com.example.whatsnew.Fragments.ApiServices
import com.example.whatsnew.Models.Chat
import com.example.whatsnew.Models.Users
import com.example.whatsnew.Notification.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageChatActivity : AppCompatActivity() {

    var userIdVisited:String = ""
    var firebaseUser:FirebaseUser? = null
    var chatAdapter:ChatAdapter? = null
    var mChatList:List<Chat>? = null
    var refernce:DatabaseReference? = null
    lateinit var recyclerView_chats: RecyclerView

    var notifiy = false
    var apiServices :ApiServices? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar:Toolbar = findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{

            val intent = Intent(this , WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        apiServices = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(ApiServices::class.java)


        intent = intent
        userIdVisited = intent.getStringExtra("visit_id").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        // Recycler view chat

        recyclerView_chats = findViewById(R.id.recycler_view_chats)
        recyclerView_chats.setHasFixedSize(true)

        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView_chats.layoutManager = linearLayoutManager

        // ...........................

         refernce = FirebaseDatabase.getInstance()
            .reference.child("Users").child(userIdVisited)

        refernce!!.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

               val user:Users? = snapshot.getValue(Users::class.java)

                username_messgae_chat.text = user!!.getUserName()
                Picasso.get().load(user.getProfile()).into(profile_image_message_chat)

                RetriveMessages(firebaseUser!!.uid , userIdVisited , user.getProfile())

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        send_messgae_btn.setOnClickListener {

            notifiy = true
            val message = text_message.text.toString()

            if(message=="")
            {
                Toast.makeText(this, "you can't send empety message , please write something", Toast.LENGTH_SHORT).show()
            }
            else
            {
                SendMessageToUser(firebaseUser!!.uid , userIdVisited , message)
            }

            text_message.setText("")

        }


        attach_image_file_btn.setOnClickListener {
            notifiy = true
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"Pick Image") , 438)
        }

        seenMessage(userIdVisited)

    }



    private fun SendMessageToUser(senderId: String, reciverId: String, message: String)
    {
        val refernce = FirebaseDatabase.getInstance().reference
        val messageKey = refernce.push().key

        val messageHashMap = HashMap<String , Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["reciver"] = reciverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        refernce.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->

                if(task.isSuccessful())
                {
                    val chatListRefernce = FirebaseDatabase.getInstance()
                        .reference.child("ChatLists")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisited)

                    chatListRefernce.addListenerForSingleValueEvent(object : ValueEventListener{

                        override fun onDataChange(snapshot: DataSnapshot) {

                            if(!snapshot.exists())
                            {
                                chatListRefernce.child("id")
                                    .setValue(userIdVisited)
                            }

                            val chatListReciverRefernce = FirebaseDatabase.getInstance()
                                .reference.child("ChatLists")
                                .child(userIdVisited)
                                .child(firebaseUser!!.uid)

                            chatListReciverRefernce.child("id")
                                .setValue(firebaseUser!!.uid)


                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

                }

            }

        // Implement notification

        val userrefernce = FirebaseDatabase.getInstance()
                .reference.child("Users").child(firebaseUser!!.uid)

        userrefernce.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(Users::class.java)

                if(notifiy)
                {
                    SendNotification(reciverId , user!!.getUserName() , message)
                }
                notifiy = false

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun SendNotification(reciverId: String, userName: String?, message: String)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")

        val query = ref.orderByKey().equalTo(reciverId)

        query.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (datasnapshot in snapshot.children)
                {
                    val token : Token? = datasnapshot.getValue(Token::class.java)
                    val data = Data(firebaseUser!!.uid ,
                            "$userName:$message",
                            "New Message",
                            userIdVisited,
                            R.mipmap.ic_launcher
                            )
                    val sender = Sender(data!! , token!!.getToken().toString())

                    apiServices!!.sendNotification(sender)
                            .enqueue(object : Callback<MyResponse>{

                                override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
                                    if(response.code()==200)
                                    {
                                        if(response.body()!!.success !==1)
                                        {
                                            Toast.makeText(this@MessageChatActivity,"Failed, Nothing happen",Toast.LENGTH_LONG).show()


                                        }
                                    }
                                }

                                override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                    TODO("Not yet implemented")
                                }

                            })


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 438 && resultCode == RESULT_OK && data !=null &&data!!.data !=null)
        {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Sending....")
            progressBar.show()

            val fileUri = data.data
            val storageRefernce = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageRefernce.child("$messageId.jpg")

            var uploadTask : StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot , Task<Uri>>{ task ->
                if(!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }

                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->

                if(task.isSuccessful)
                {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String , Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "sent you an image"
                    messageHashMap["reciver"] = userIdVisited
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                            .addOnCompleteListener { task ->

                                if(task.isSuccessful)
                                {
                                    progressBar.dismiss()

                                    // Implement notification
                                    val refernce = FirebaseDatabase.getInstance()
                                            .reference.child("Users").child(firebaseUser!!.uid)

                                    refernce.addValueEventListener(object : ValueEventListener{

                                        override fun onDataChange(snapshot: DataSnapshot) {

                                            val user = snapshot.getValue(Users::class.java)

                                            if(notifiy)
                                            {
                                                SendNotification(userIdVisited , user!!.getUserName() , "sent you an image")
                                            }
                                            notifiy = false

                                        }

                                        override fun onCancelled(error: DatabaseError) {

                                        }

                                    })
                                }
                            }

                }


            }

        }

    }

    private fun RetriveMessages(senderUID: String, reciverUID: String, reciverImageUrl: String?)
    {

        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                (mChatList as ArrayList<Chat>).clear()
                for (datasnapshot in snapshot.children)
                {
                    val chat = datasnapshot.getValue(Chat::class.java)

                    if(chat!!.getReciver().equals(senderUID) && chat.getSender().equals(reciverUID)
                            || chat.getReciver().equals(reciverUID) && chat.getSender().equals(senderUID))
                    {
                        (mChatList as ArrayList<Chat>).add(chat)

                    }
                    chatAdapter = ChatAdapter(applicationContext ,  (mChatList as ArrayList<Chat>) , reciverImageUrl!! )
                    recyclerView_chats.adapter = chatAdapter
                    chatAdapter!!.notifyDataSetChanged()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    var seenListner:ValueEventListener? = null

    private fun seenMessage(usserId:String)
    {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(datasnapshot in snapshot.children)
                {
                    val chat = datasnapshot.getValue(Chat::class.java)

                    if(chat!!.getReciver().equals(firebaseUser!!.uid) && chat!!.getSender().equals(usserId))
                    {
                        val hashMap = HashMap<String , Any>()
                        hashMap["isseen"] = true
                        datasnapshot.ref.updateChildren(hashMap)

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onPause() {
        super.onPause()

        refernce!!.removeEventListener(seenListner!!)
    }


}