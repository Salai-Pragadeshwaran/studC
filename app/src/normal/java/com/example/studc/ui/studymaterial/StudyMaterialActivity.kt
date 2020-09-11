package com.example.studc.ui.studymaterial.ui.studymaterial

import android.app.ProgressDialog
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studc.R
import com.google.firebase.firestore.FirebaseFirestore

//import com.example.studc.ui.studymaterial.R

class StudyMaterialActivity : AppCompatActivity() {

    companion object {
        fun newInstance() = StudyMaterialActivity()
    }

    private lateinit var viewModel: StudyMaterialViewModel
    lateinit var db : FirebaseFirestore
    lateinit var studyMaterialRecycler : RecyclerView
    var studyMaterials = ArrayList<StudyMaterial>()
    lateinit var studyMaterialAdapter : StudyMaterialAdapter
    lateinit var subjectCode : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.study_material_fragment)

        subjectCode = intent.getStringExtra("SUB_CODE")

        db = FirebaseFirestore.getInstance()
        studyMaterialRecycler = findViewById(com.example.studc.R.id.studyMaterialRecycler)
        studyMaterialRecycler.layoutManager = LinearLayoutManager(this)


        var pd = ProgressDialog(this)
        pd.setTitle("Loading Subjects")
        pd.show()
        dataFromFirebase()
        pd.dismiss()
    }


    //         fetching data from firestore
    fun dataFromFirebase() {
        if(studyMaterials.size>0)
            studyMaterials.clear()

        db.collection("files")
            .whereEqualTo("SUB_CODE", subjectCode)
            .get()
            .addOnSuccessListener { result ->
                if(result.size()==0){
                    Toast.makeText(this, "No Material Available", Toast.LENGTH_SHORT).show()
                }
                for (documentSnapshot in result) {
                    var studyMaterial = StudyMaterial(name = documentSnapshot.getString("name")!!, link = documentSnapshot.getString("link")!!)
                    studyMaterials.add(studyMaterial)
                }

                var myClipboard =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                studyMaterialAdapter = StudyMaterialAdapter(studyMaterials, this, myClipboard)
                studyMaterialRecycler.adapter = studyMaterialAdapter
            }
            .addOnFailureListener { _ ->
                Toast.makeText(this, "Error ;-.-;", Toast.LENGTH_SHORT).show()
            }


//        db.collection("files")
//            .get()
//            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> {
//                @Override
//                fun onComplete(@NonNull task: Task<QuerySnapshot>) {
//                    for(documentSnapshot in task.result!!) {
//
//                        var studyMaterial = StudyMaterial(name = documentSnapshot.getString("name")!!, link = documentSnapshot.getString("link")!!)
//                        studyMaterials.add(studyMaterial)
//
//                    }
//
//                    studyMaterialAdapter = StudyMaterialAdapter(studyMaterials, this.context!!)
//                    studyMaterialRecycler.adapter = studyMaterialAdapter
//                }
//            })
//
//            .addOnFailureListener(OnFailureListener() {
//                @Override
//                fun onFailure(@NonNull e: Exception) {
//                    Toast.makeText(this.context, "Error ;-.-;", Toast.LENGTH_SHORT).show()
//                }
//            })


    }

}