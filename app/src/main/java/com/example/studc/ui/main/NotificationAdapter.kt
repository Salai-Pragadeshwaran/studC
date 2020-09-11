package com.example.studc.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.R
import kotlinx.android.synthetic.main.notification_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(val notifications: java.util.ArrayList<com.example.studc.ui.main.Notification>, private val mcontext: Context)
    :RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        internal var usernameTextView: TextView
        internal var postTextView: TextView
        internal var userImage: ImageView
        internal var time: TextView

        init {
            usernameTextView = itemView.postUsername
            postTextView = itemView.postText
            userImage = itemView.postImage
            time = itemView.postTime
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(com.example.studc.R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.usernameTextView.setText(notifications[position].username)
        holder.postTextView.setText(notifications[position].postText)
        val imageUri = notifications[position].imgUrl
        Glide.with(mcontext).clear(holder.userImage)
        Glide.with(mcontext)
            .asBitmap()
            .load(imageUri)
            .circleCrop()
            .placeholder(com.example.studc.R.drawable.ic_launcher_foreground)
            .into(holder.userImage)


        var cal : Calendar = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis= notifications[position].postTime
        var sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy\nHH:mm:ss", Locale.getDefault())
        holder.time.text = sdf.format(cal.timeInMillis)

    }
}