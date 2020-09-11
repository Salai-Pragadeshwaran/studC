package com.example.studc.ui.main

import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studc.BuildConfig.IS_ADMIN
import com.example.studc.MainActivity
import com.example.studc.UserInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var mFirebaseDatabase : FirebaseDatabase
    private lateinit var mDatabaseReference : DatabaseReference
    private lateinit var rollDatabaseReference : DatabaseReference
    private var posts = ArrayList<Notification>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(com.example.studc.R.layout.main_fragment, container, false)

        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase.reference.child("Feed")
        rollDatabaseReference = mFirebaseDatabase.reference.child("RollNo")


        var pd = ProgressDialog(root.context)
        pd.setTitle("Loading Notifications")
        pd.show()
        var mChildEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                var notifications =
                    dataSnapshot.getValue<Notification>()
                if (notifications != null) {
                    posts.add(notifications)
                }
                var notificationsRecycler: RecyclerView = root.findViewById(com.example.studc.R.id.notificationsRecycler)
                var notificationAdapter = NotificationAdapter(posts, root.context)
                notificationsRecycler.adapter = notificationAdapter
                var feedsLayoutManager = LinearLayoutManager(root.context)
                feedsLayoutManager.reverseLayout=false
                notificationsRecycler.layoutManager = feedsLayoutManager
                pd.dismiss()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        mDatabaseReference.addChildEventListener(mChildEventListener)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

}