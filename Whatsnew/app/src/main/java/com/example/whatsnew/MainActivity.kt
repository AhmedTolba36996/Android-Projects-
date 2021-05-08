package com.example.whatsnew

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TableLayout
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.whatsnew.Fragments.ChatFragment
import com.example.whatsnew.Fragments.SearchFragment
import com.example.whatsnew.Fragments.SettingsFragment
import com.example.whatsnew.Models.Chat
import com.example.whatsnew.Models.Users
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var refUsers : DatabaseReference?=null
    var firebaseUser : FirebaseUser ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        // Toolbar With Fragment
        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        val tabLayout:TabLayout = findViewById(R.id.tab_layout)
        val viewpager:ViewPager = findViewById(R.id.view_pager)

        // Moving From Fragment to anthor ******************
//        val viewpagerAdapter = ViewPagerAdapter(supportFragmentManager)
//        viewpagerAdapter.AddFragment(ChatFragment(),"Chats")
//        viewpagerAdapter.AddFragment(SearchFragment(),"Search")
//        viewpagerAdapter.AddFragment(SettingsFragment(),"Settings")
//
//        viewpager.adapter = viewpagerAdapter
//        tabLayout.setupWithViewPager(viewpager)
        //*********************************************************

        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val viewpagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var countUnReadMeesages = 0

                for(datasnapshot in snapshot.children)
                {
                    val chat = datasnapshot.getValue(Chat::class.java)
                    if(chat!!.getReciver().equals(firebaseUser!!.uid) && !chat.isIsSeen())
                    {
                        countUnReadMeesages = countUnReadMeesages + 1
                    }
                }

                if(countUnReadMeesages==0)
                {
                    viewpagerAdapter.AddFragment(ChatFragment(),"Chats")
                }
                else
                {
                    viewpagerAdapter.AddFragment(ChatFragment() , "($countUnReadMeesages) Chats")
                }

                viewpagerAdapter.AddFragment(SearchFragment(),"Search")
                viewpagerAdapter.AddFragment(SettingsFragment(),"Settings")

                viewpager.adapter = viewpagerAdapter
                tabLayout.setupWithViewPager(viewpager)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        // Display Username and Profile  Picture
        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot)
            {
                if (snapshot.exists())
                {

                    val user:Users? = snapshot.getValue(Users::class.java)
                    user_name.text = user!!.getUserName()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.ic_profile).into(profile_image)

                }
            }

            override fun onCancelled(error: DatabaseError)
            {

            }

        })

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
         when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity , WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

                startActivity(intent)
                finish()
                return true
            }
        }
        return false
    }

    // View Pager Fragemt *****************************************************
    internal class ViewPagerAdapter(fragmentManger:FragmentManager):
        FragmentPagerAdapter(fragmentManger)
    {

        private val fragments: ArrayList<Fragment>
        private val titles:ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }



        override fun getItem(position: Int): Fragment
        {
            return fragments[position]
        }

        override fun getCount(): Int
        {
           return fragments.size
        }

        fun AddFragment(fragment:Fragment , title:String)
        {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        //***************************************************
    }
    private fun UpdateStatus(status:String)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        val hashMap = HashMap<String , Any>()
        hashMap["status"] = status
        ref!!.updateChildren(hashMap)
    }

    override fun onRestart() {
        super.onRestart()
        UpdateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        UpdateStatus("Offline")
    }
}