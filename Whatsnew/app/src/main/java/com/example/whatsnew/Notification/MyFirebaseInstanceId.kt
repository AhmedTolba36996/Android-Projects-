package com.example.whatsnew.Notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId : FirebaseMessagingService()
{
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val refeshToken = FirebaseInstanceId.getInstance().token

        if(firebaseUser!=null)
        {
            UpdateToken(refeshToken)
        }

    }

    private fun UpdateToken(refeshToken: String?)
    {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().getReference().child("Tokens")
        val token = Token(refeshToken!!)
        ref.child(firebaseUser!!.uid).setValue(token)
    }
}