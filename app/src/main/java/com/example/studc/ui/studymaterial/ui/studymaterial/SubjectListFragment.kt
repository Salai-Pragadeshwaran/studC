package com.example.studc.ui.studymaterial.ui.studymaterial

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studc.R
import com.example.studc.ui.main.Notification
import com.example.studc.ui.main.NotificationAdapter
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue


class SubjectListFragment : Fragment() {

    private lateinit var mFirebaseDatabase : FirebaseDatabase
    private lateinit var mDatabaseReference : DatabaseReference

    lateinit var subjectRecycler : RecyclerView
    var subjects = ArrayList<Subject>()
    lateinit var recAdapter : SubjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var root =
            inflater.inflate(com.example.studc.R.layout.fragment_subject_list, container, false)

        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase.reference.child("Subjects")

//        mDatabaseReference.push().setValue(Subject(name = "ECPC"))
//        mDatabaseReference.push().setValue(Subject(name = "ECIR"))
//        mDatabaseReference.push().setValue(Subject(name = "HSIR"))
//        mDatabaseReference.push().setValue(Subject(name = "MAIR"))


        subjectRecycler = root.findViewById(com.example.studc.R.id.subjectRecycler)
        subjectRecycler.layoutManager = LinearLayoutManager(root.context)

        var pd = ProgressDialog(root.context)
        pd.setTitle("Loading Subjects")
        pd.show()
        var mChildEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                var sub =
                    dataSnapshot.getValue<Subject>()
                if (sub != null) {
                    subjects.add(sub)
                }
                populateRecyclerView()
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

    fun populateRecyclerView(){
        recAdapter = SubjectAdapter(subjects, this.context!!)
        subjectRecycler.adapter = recAdapter
    }

        companion object {

        fun newInstance() = SubjectListFragment()
    }
}