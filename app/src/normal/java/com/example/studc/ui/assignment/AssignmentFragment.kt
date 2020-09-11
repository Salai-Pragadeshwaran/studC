package com.example.studc.ui.assignment

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studc.R
import com.example.studc.ui.studymaterial.ui.studymaterial.StudyMaterial
import com.example.studc.ui.studymaterial.ui.studymaterial.StudyMaterialAdapter
import com.example.studc.ui.studymaterial.ui.studymaterial.Subject
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore


class AssignmentFragment : Fragment() {

    companion object {
        fun newInstance() =
            AssignmentFragment()
    }

    private lateinit var mFirebaseDatabase : FirebaseDatabase
    private lateinit var mDatabaseReference : DatabaseReference

    lateinit var assignmentRecycler: RecyclerView
    var assignments = ArrayList<AssignmentInfo>()
    lateinit var db : FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_assignment, container, false)

        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase.reference.child("Assignments")

//        mDatabaseReference.push().setValue(AssignmentInfo(name = "check1", subject = "ECIR"))
//        mDatabaseReference.push().setValue(AssignmentInfo(name = "check2", subject = "HSIR"))
//        mDatabaseReference.push().setValue(AssignmentInfo(name = "check3", subject = "MAIR"))


        db = FirebaseFirestore.getInstance()

        var pd = ProgressDialog(root.context)
        pd.setTitle("Loading Assignments")
        pd.show()
        db.collection("Assignments")
            .get()
            .addOnSuccessListener { result ->
                for (documentSnapshot in result) {
                    var assignment = AssignmentInfo(name = documentSnapshot.getString("name")!!, link = documentSnapshot.getString("link")!!,
                    extension = documentSnapshot.getString("extension")!!, subject = documentSnapshot.getString("subject")!!)
                    assignments.add(assignment)
                }
                populateRecyclerView(root)
                pd.dismiss()
            }
            .addOnFailureListener { _ ->
                Toast.makeText(root.context, "Error ;-.-;", Toast.LENGTH_SHORT).show()
                pd.dismiss()
            }

        return root
    }

    private fun populateRecyclerView(root: View) {
        assignmentRecycler = root.findViewById(com.example.studc.R.id.assignmentsRecycler)
        assignmentRecycler.layoutManager = LinearLayoutManager(root.context)
        var recAdapter = AssignmentAdapter(assignments, this.context!!)
        assignmentRecycler.adapter = recAdapter
    }


}