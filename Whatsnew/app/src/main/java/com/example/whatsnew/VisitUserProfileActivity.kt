package com.example.whatsnew

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.UserHandle
import com.example.whatsnew.Models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.android.synthetic.main.activity_visit_user_profile.*

class VisitUserProfileActivity : AppCompatActivity() {

    private var userVistedId:String=""
    private var user:Users? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profile)

        userVistedId = intent.getStringExtra("visit_id").toString()

        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userVistedId)

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists())
                {
                    user = snapshot.getValue(Users::class.java)
                    username_display.text = user!!.getUserName()
                    Picasso.get().load(user!!.getProfile()).into(profile_image_display)
                    Picasso.get().load(user!!.getCover()).into(cover_image_display)


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        face_book_display.setOnClickListener {
            val url = Uri.parse(user!!.getFaceBook())
            val intent = Intent(Intent.ACTION_VIEW,url)
            startActivity(intent)
        }
        instgram_display.setOnClickListener {
            val url = Uri.parse(user!!.getInstgram())
            val intent = Intent(Intent.ACTION_VIEW,url)
            startActivity(intent)
        }

        send_img.setOnClickListener {
            val intent = Intent(this@VisitUserProfileActivity, MessageChatActivity::class.java)
            intent.putExtra("visit_id" , user!!.getUID())
            startActivity(intent)
        }
    }
    
}