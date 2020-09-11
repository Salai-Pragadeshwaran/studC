package com.example.studc.ui.studymaterial.ui.studymaterial

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.subject_item.view.*

class SubjectAdapter(val subjects: java.util.ArrayList<Subject>, private val mcontext: Context)
    : RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var nameTextView: TextView
        internal var subjectConstraintLayout: ConstraintLayout

        init {
            nameTextView = itemView.subjectName
            subjectConstraintLayout = itemView.subjectBg
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(com.example.studc.R.layout.subject_item, parent, false)
        return SubjectAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subjects.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.setText(subjects[position].name)
        holder.subjectConstraintLayout.setOnClickListener {
            var intent = Intent (mcontext, StudyMaterialActivity::class.java)
            intent.putExtra("SUB_CODE", subjects[position].name)
            mcontext.startActivity(intent)
        }
    }
}