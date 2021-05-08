package com.example.whatsnew.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsnew.MessageChatActivity
import com.example.whatsnew.Models.Chat
import com.example.whatsnew.Models.Users
import com.example.whatsnew.R
import com.example.whatsnew.VisitUserProfileActivity
import com.example.whatsnew.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_search_item.view.*

class UserAdapter(mContext: Context,
                  mUsers:List<Users>,
                  isChatCheck:Boolean
        ): RecyclerView.Adapter<UserAdapter.ViewHolder?>()

{

    private val mContext: Context
    private val  mUsers:List<Users>
    private var isChatCheck:Boolean
    var lMessage:String = ""

    init {
        this.mContext = mContext
        this.mUsers=mUsers
        this.isChatCheck=isChatCheck
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder
    {
        val view : View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item , viewGroup , false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user:Users? = mUsers[position]
        holder.userNameTxt.text = user!!.getUserName()

        Picasso.get().load(user.getProfile()).placeholder(R.drawable.ic_profile).into(holder.profileImageView)

        if(isChatCheck)
        {
            RetriveLastMessage(user.getUID() , holder.lastMessageTxt)
        }
        else
        {
            holder.lastMessageTxt.visibility = View.GONE
        }

        if(isChatCheck)
        {
            if(user.getStatus()=="online")
            {
                holder.onlineImageView.visibility = View.VISIBLE
                holder.offliineImageView.visibility = View.GONE
            }
            else
            {
                holder.offliineImageView.visibility = View.VISIBLE
                holder.onlineImageView.visibility = View.GONE
            }
        }
        else
        {
            holder.offliineImageView.visibility = View.GONE
            holder.onlineImageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Send Message",
                "Visit Profile"
            )

            val builder:AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("What do you want ? ")
            builder.setItems(options , DialogInterface.OnClickListener{ dialog, position ->

                if(position==0)
                {
                    val intent = Intent(mContext , MessageChatActivity::class.java)
                    intent.putExtra("visit_id" , user.getUID())
                    mContext.startActivity(intent)
                }
                if(position==1)
                {
                    val intent = Intent(mContext , VisitUserProfileActivity::class.java)
                    intent.putExtra("visit_id" , user.getUID())
                    mContext.startActivity(intent)
                }
            })
            builder.show()
        }


    }

    private fun RetriveLastMessage(chatUseruid: String?, lastMessageTxt: TextView)
    {
        lMessage = "What's new Chat"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refernce = FirebaseDatabase.getInstance().reference.child("Chats")

        refernce.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for (datasnapshot in snapshot.children)
                {
                    val chat:Chat? = datasnapshot.getValue(Chat::class.java)
                    if(firebaseUser!=null && chat!=null)
                    {
                        if(chat.getReciver()==firebaseUser!!.uid &&
                            chat.getSender()==chatUseruid ||
                                chat.getReciver()==chatUseruid &&
                                    chat.getSender()==firebaseUser!!.uid
                                )
                        {
                            lMessage = chat.getMessage()!!
                        }
                    }
                }
                when(lMessage)
                {
                    "What's new Chat"->lastMessageTxt.text="No Message yet"
                    "sent you an image"->lastMessageTxt.text="Sent Image"
                    else -> lastMessageTxt.text = lMessage
                }
                lMessage="What's new Chat"

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    class ViewHolder(itemView: View ):RecyclerView.ViewHolder(itemView)
    {
        var userNameTxt:TextView
        var lastMessageTxt:TextView
        var profileImageView:CircleImageView
        var onlineImageView:CircleImageView
        var offliineImageView:CircleImageView

        init {
            userNameTxt = itemView.findViewById(R.id.user_name_item)
            lastMessageTxt = itemView.findViewById(R.id.last_messge_item);
            profileImageView = itemView.findViewById(R.id.profile_image_item);
            onlineImageView = itemView.findViewById(R.id.online_image_item);
            offliineImageView = itemView.findViewById(R.id.offLine_image_item);
        }

    }


}