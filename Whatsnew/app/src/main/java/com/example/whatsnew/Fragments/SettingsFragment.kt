package com.example.whatsnew.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.whatsnew.Models.Users
import com.example.whatsnew.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    var userResfernce:DatabaseReference? = null
    var firebaseUSer:FirebaseUser? = null
    private val requsetCode = 438
    private var imageUri:Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker:String? = ""
    private var socialChecker:String? = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUSer = FirebaseAuth.getInstance().currentUser
        userResfernce = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUSer!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        userResfernce!!.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists())
                {
                    val user:Users? = snapshot.getValue(Users::class.java)

                    if(context!=null)
                    {
                        view.username_setting.text = user!!.getUserName()
                        Picasso.get().load(user!!.getProfile()).into(view.profile_image_setting)
                        Picasso.get().load(user!!.getCover()).into(view.cover_image_setting)

                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        view.profile_image_setting.setOnClickListener{
            PickImage()
        }

        view.cover_image_setting.setOnClickListener {
            coverChecker = "cover"
            PickImage()
        }

        view.face_book_setting.setOnClickListener {
            socialChecker = "facebook"
            SetSocialLink()
        }

        view.instgram_setting.setOnClickListener {
            socialChecker = "instgram"
            SetSocialLink()
        }

        return view
    }

    private fun SetSocialLink() {

        val builder:AlertDialog.Builder =
                AlertDialog.Builder(context,R.style.Theme_AppCompat_DayNight_Dialog_Alert)

        builder.setTitle("Enter Profile Link")

        val editText = EditText(context)
        editText.hint = "e.g https://www.instagram.com/ahmed.tolba_/"
        builder.setView(editText)

        builder.setPositiveButton("Create" , DialogInterface.OnClickListener{
            dialog, which ->

            val str = editText.text.toString()
            if(str=="")
            {
                Toast.makeText(context , "Please Write Username" , Toast.LENGTH_LONG).show()
            }
            else
            {
                SaveSocialLink(str)
            }


        })
        builder.setNegativeButton("Cancel" , DialogInterface.OnClickListener{
            dialog, which ->
            dialog.cancel()
        })

        builder.show()

    }

    private fun SaveSocialLink(str: String)
    {
        val mapSocial = HashMap<String , Any>()

//        mapSocial["cover"] = url
//        userResfernce!!.updateChildren(mapSocial)
//        coverChecker = ""

        when(socialChecker)
        {
            "facebook" ->
            {
                mapSocial["facebook"] = "$str"
            }
            "instgram"->
            {
                mapSocial["instgram"] = "$str"
            }
        }

        userResfernce!!.updateChildren(mapSocial).addOnCompleteListener {
            task ->

            if(task.isSuccessful)
            {
                Toast.makeText(context , "Updated Successfully" , Toast.LENGTH_LONG).show()
            }
        }


    }


    // Open Gallery to select image
    private fun PickImage()
    {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent , requsetCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == requsetCode && resultCode == Activity.RESULT_OK && data!!.data !=null)
        {
            imageUri = data.data
            Toast.makeText(context,"Uploading..",Toast.LENGTH_LONG).show()
            UploadImage()
        }

    }

    private fun UploadImage()
    {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Uploading Please Wait....")
        progressBar.show()

        if(imageUri!=null)
        {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask :StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot , Task<Uri>>{ task ->
                if(!task.isSuccessful)
                {
                    task.exception?.let {
                        throw it
                    }

                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful)
                {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if(coverChecker == "cover")
                    {
                        val mapCoverImg = HashMap<String , Any>()
                        mapCoverImg["cover"] = url
                        userResfernce!!.updateChildren(mapCoverImg)
                        coverChecker = ""

                    }
                    else
                    {
                        val mapProfileImg = HashMap<String , Any>()
                        mapProfileImg["profile"] = url
                        userResfernce!!.updateChildren(mapProfileImg)
                        coverChecker = ""
                    }

                    progressBar.dismiss()

                }
            }
        }



    }


}