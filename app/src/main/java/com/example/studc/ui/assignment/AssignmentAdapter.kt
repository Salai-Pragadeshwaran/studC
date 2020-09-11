package com.example.studc.ui.assignment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.studc.MainActivity
import com.example.studc.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.assignment_list.view.*

class AssignmentAdapter(val assignments: ArrayList<AssignmentInfo>, private val mcontext: Context):
    RecyclerView.Adapter<AssignmentAdapter.ViewHolder>() {

    class ViewHolder( itemView: View): RecyclerView.ViewHolder(itemView){
        internal var infoTextView: TextView
        internal var deadlineTextView: TextView
        internal var assignmentContainer: ConstraintLayout
        init{
            infoTextView = itemView.assignmentInfo
            deadlineTextView = itemView.deadline
            assignmentContainer = itemView.assignmentContainer
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(com.example.studc.R.layout.assignment_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return assignments.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.infoTextView.text = assignments[position].subject + " " + assignments[position].name
        holder.deadlineTextView.text = assignments[position].deadline.toString()
        assignmentSubmitted(assignments[position].subject, assignments[position].name, holder.assignmentContainer)
        holder.assignmentContainer.setOnClickListener {
            var intent = Intent (mcontext, AssignmentSubmissionActivity::class.java)
            intent.putExtra("SUB_CODE", assignments[position].subject)
            intent.putExtra("NAME", assignments[position].name)
            intent.putExtra("LINK", assignments[position].link)
            intent.putExtra("EXTENSION", assignments[position].extension)
            intent.putExtra("DEADLINE", assignments[position].deadline)
            intent.putExtra("MARKS", assignments[position].marks)
            mcontext.startActivity(intent)
        }
    }

    private fun assignmentSubmitted(
        subject: String,
        name: String,
        assignmentContainer: ConstraintLayout
    ){
        var mFirebaseDatabase = FirebaseDatabase.getInstance()
        var mDatabaseReference = mFirebaseDatabase.reference.child("Assignments").child("${subject} ${name}")



        mDatabaseReference.child(MainActivity.rollNo.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("submissionTime").value != null) {
                    //TODO check if assignment is submitted within time
                    assignmentContainer.setBackgroundResource(R.color.assignment_submitted)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

}