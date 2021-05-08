package com.example.whatsnew.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsnew.Models.Chat
import com.example.whatsnew.Models.Users
import com.example.whatsnew.R
import com.example.whatsnew.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.messsage_item_left.view.*

class ChatAdapter (mContext: Context,
                   mChatList:List<Chat>,
                   imageUrl:String
): RecyclerView.Adapter<ChatAdapter.ViewHolder?>()

{

    private val mContext:Context
    private val mChatList:List<Chat>
    private val imageUrl:String

    var firebaseUser:FirebaseUser = FirebaseAuth.getInstance().currentUser!!


    init {
        this.mContext = mContext
        this.mChatList = mChatList
        this.imageUrl = imageUrl
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
       return if(position==1)
       {
           val view : View = LayoutInflater.from(mContext).inflate(com.example.whatsnew.R.layout.message_item_right , parent , false)
           ViewHolder(view)
       }
        else
       {
           val view : View = LayoutInflater.from(mContext).inflate(R.layout.messsage_item_left , parent , false)
           ViewHolder(view)
       }
    }

    override fun getItemCount(): Int {
       return mChatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat:Chat = mChatList[position]

        // Profile image
        Picasso.get().load(imageUrl).into(holder.profile_image)
       // Toast.makeText(mContext , "Image path " + chat.getURL() , Toast.LENGTH_LONG).show()


        // Image messges
        if(chat.getMessage().equals("sent you an image") && !chat.getURL().equals(""))
        {
            // image message right side
            if(chat.getSender().equals(firebaseUser!!.uid)) {
                holder.text_show_message!!.visibility = View.GONE
                holder.right_imageview_mes!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getURL()).into(holder.right_imageview_mes)

                holder.right_imageview_mes!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                            "View Full Image",
                            "Delete Image",
                            "Cancel"
                    )

                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("What do you want ? ")
                    builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->

                        if (position == 0)
                        {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.getURL())
                            mContext.startActivity(intent)

                        }
                        else if (position == 1)
                        {
                            DeletSentMessage(position, holder)

                        }

                    })
                    builder.show()
                }
            }


            // image message Left side
            else if(!chat.getSender().equals(firebaseUser!!.uid))
            {
                holder.text_show_message!!.visibility = View.GONE
                holder.left_imageview_mes!!.visibility  =View.VISIBLE
                Picasso.get().load(chat.getURL()).into(holder.left_imageview_mes)

                holder.left_imageview_mes!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                            "View Full Image",

                            "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)

                    builder.setTitle("What do you want ?")

                    builder.setItems(options,DialogInterface.OnClickListener{
                        dialog, which ->
                        if(which==0)
                        {
                            val intent = Intent(mContext , ViewFullImageActivity::class.java)
                            intent.putExtra("url" , chat.getURL())
                            mContext.startActivity(intent)

                        }

                    })
                   builder.show()
                }

            }
        }
        // Text message
        else
        {
            holder.text_show_message!!.text  =chat.getMessage()

            if(firebaseUser!!.uid == chat.getSender())
            {

                holder.text_show_message!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                            "Delete Message",
                            "Cancel"
                    )
                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)

                    builder.setTitle("What do you want ?")

                    builder.setItems(options,DialogInterface.OnClickListener{
                        dialog, which ->

                        if (which==0)
                        {
                            DeletSentMessage(position,holder)
                        }

                    })
                    builder.show()
                }
            }

        }

        // Seen message
        if(position == mChatList.size-1)
        {
           if(chat.isIsSeen())
           {
               holder.text_seen!!.text = "Seen"
               if(chat.getMessage().equals("sent you an image") && !chat.getURL().equals(""))
               {
                   val lp:RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                   lp!!.setMargins(0,245,10,0)
                   holder.text_seen!!.layoutParams = lp

               }
           }
            else
           {
               holder.text_seen!!.text = "Sent"
               if(chat.getMessage().equals("sent you an image") && !chat.getURL().equals(""))
               {
                   val lp:RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                   lp!!.setMargins(0,245,10,0)
                   holder.text_seen!!.layoutParams = lp

               }
           }
        }
        else
        {
            holder.text_seen!!.visibility = View.GONE
        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var profile_image : CircleImageView? = null
        var text_show_message : TextView? = null

        var left_imageview_mes : ImageView? = null
        var text_seen : TextView? = null

        var right_imageview_mes : ImageView? = null

        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            text_show_message = itemView.findViewById(R.id.text_show_message)
            left_imageview_mes = itemView.findViewById(R.id.left_imageview)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_imageview_mes = itemView.findViewById(R.id.right_imageview)

        }

    }

    override fun getItemViewType(position: Int): Int {

        return if(mChatList[position].getSender().equals(firebaseUser!!.uid))
        {
            1
        }
        else
        {
            0
        }

    }

    private fun DeletSentMessage(position: Int , holder:ChatAdapter.ViewHolder)
    {
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
                .child(mChatList.get(position).getMessageId()!!)
                .removeValue()
                .addOnCompleteListener { task ->
                    if(task.isSuccessful)
                    {
                        Toast.makeText(holder.itemView.context , "Deleted" , Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(holder.itemView.context , "Fail Deleted" , Toast.LENGTH_SHORT).show()
                    }
                }
    }


}