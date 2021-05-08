package com.example.whatsnew.Adapters

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.whatsnew.R
import com.squareup.picasso.Picasso

class ViewFullImageActivity : AppCompatActivity() {

    private var image_viewr:ImageView? = null
    private var imageurl:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_image)

        imageurl = intent.getStringExtra("url").toString()
        image_viewr = findViewById(R.id.show_full_image)

        Picasso.get().load(imageurl).into(image_viewr)

    }
}