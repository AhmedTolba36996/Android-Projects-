package com.example.whatsnew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseuserID : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        // Toolbar
        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this@RegisterActivity , WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        register_btn.setOnClickListener {
            RegisterUser()
        }



    }

    private fun RegisterUser()
    {
        val username:String = username_register.text.toString()
        val email:String = email_register.text.toString()
        val password:String = password_register.text.toString()

        if(username == "")
        {
            Toast.makeText(this, "plese enter username", Toast.LENGTH_SHORT).show()
        }
        else if (email == "")
        {
            Toast.makeText(this, "please enetr email", Toast.LENGTH_SHORT).show()
        }
        else if (password=="")
        {
            Toast.makeText(this, "please enter password", Toast.LENGTH_SHORT).show()
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful)
                    {

                        firebaseuserID = mAuth.currentUser!!.uid
                        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseuserID)

                        val userHashMap = HashMap<String , Any>()
                        userHashMap["uid"] = firebaseuserID
                        userHashMap["username"] = username
                        userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/chat-7f846.appspot.com/o/profile_image.png?alt=media&token=58ee1a3e-18b7-4075-81ca-689f977a7b01"
                        userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/chat-7f846.appspot.com/o/cover.jfif?alt=media&token=82d71f50-f476-4ce9-b5e5-704744d0c26d"
                        userHashMap["status"] = "Offline"
                        userHashMap["search"] = username.toLowerCase()
                        userHashMap["facebook"] = "https://www.facebook.com/profile.php?id=100003736635609"
                        userHashMap["instgram"] = "https://www.instagram.com/ahmed.tolba_/"

                        refUsers.updateChildren(userHashMap)
                            .addOnCompleteListener{task ->

                                Toast.makeText(this, "Welcome At What's new", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@RegisterActivity , MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                                startActivity(intent)
                                finish()

                            }

                    }
                    else
                    {
                        Toast.makeText(this, "Error" + task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
        }

    }
}