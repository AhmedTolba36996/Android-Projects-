package com.example.whatsnew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Toolbar With Fragment
        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this@LoginActivity , WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        login_btn.setOnClickListener {
            LoginUser()
        }

    }

    private fun LoginUser()
    {
        val email:String = email_login.text.toString()
        val password:String = password_login.text.toString()

        if (email == "")
        {
            Toast.makeText(this, "please enetr email", Toast.LENGTH_SHORT).show()
        }
        else if (password=="")
        {
            Toast.makeText(this, "please enter password", Toast.LENGTH_SHORT).show()
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        val intent = Intent(this@LoginActivity , MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this, "This account not found , Please creat an account  ", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }


}