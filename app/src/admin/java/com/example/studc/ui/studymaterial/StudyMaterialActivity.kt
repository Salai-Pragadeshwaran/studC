package com.example.studc.ui.studymaterial.ui.studymaterial

import android.app.ProgressDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studc.MainActivity
import com.example.studc.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.admin.study_material_fragment.*
import kotlinx.android.synthetic.main.activity_assignment_submission.*
import java.io.File

//import com.example.studc.ui.studymaterial.R

class StudyMaterialActivity : AppCompatActivity() {

    companion object {
        fun newInstance() = StudyMaterialActivity()
    }

    private lateinit var viewModel: StudyMaterialViewModel
    lateinit var db: FirebaseFirestore
    lateinit var studyMaterialRecycler: RecyclerView
    var studyMaterials = ArrayList<StudyMaterial>()
    lateinit var studyMaterialAdapter: StudyMaterialAdapter
    lateinit var subjectCode: String
    lateinit var extension: String
    lateinit var link: String
    lateinit var name: String
    var selectedFile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.study_material_fragment)

        subjectCode = intent.getStringExtra("SUB_CODE")

        db = FirebaseFirestore.getInstance()
        studyMaterialRecycler = findViewById(com.example.studc.R.id.studyMaterialRecycler)
        studyMaterialRecycler.layoutManager = LinearLayoutManager(this)

        addStudyMaterial.setOnClickListener {
            //upload study material
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }

        var pd = ProgressDialog(this)
        pd.setTitle("Loading Subjects")
        pd.show()
        dataFromFirebase()
        pd.dismiss()
    }


    //         fetching data from firestore
    fun dataFromFirebase() {
        if (studyMaterials.size > 0)
            studyMaterials.clear()

        db.collection("files")
            .whereEqualTo("SUB_CODE", subjectCode)
            .get()
            .addOnSuccessListener { result ->
                if (result.size() == 0) {
                    Toast.makeText(this, "No Material Available", Toast.LENGTH_SHORT).show()
                }
                for (documentSnapshot in result) {
                    var studyMaterial = StudyMaterial(
                        name = documentSnapshot.getString("name")!!,
                        link = documentSnapshot.getString("link")!!
                    )
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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            if (data?.data !== null) {
                selectedFile = data?.data!!
                name = DocumentFile.fromSingleUri(this, selectedFile!!)?.name!!
                extension = DocumentFile.fromSingleUri(this, selectedFile!!)?.type!!
//                fileSelected.text = "Location of selected File - $selectedFile"
                // TODO: add extra admin check here
                if (MainActivity.rollNo != null) {
                    uploadFile()
                } else {
                    Toast.makeText(
                        this,
                        "Access denied - You are not Authorised",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }


    private fun uploadFile() {
        if (selectedFile != null) {
            var pd = ProgressDialog(this)
            pd.setTitle("Uploading")
            pd.show()

            var docRef =
                FirebaseStorage.getInstance().reference.child("$name")
            docRef.putFile(selectedFile!!)
                .addOnSuccessListener {
                    pd.dismiss()
                    Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()
                    var studyMaterialInfo = hashMapOf(
                        "SUB_CODE" to subjectCode,
                        "extension" to extension,
                        "link" to docRef.downloadUrl.toString(),
                        "name" to name
                    )

                    db.collection("files").document()
                        .set(studyMaterialInfo)
                        .addOnSuccessListener { Log.d("StudyMaterialActivity", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("StudyMaterialActivity", "Error writing document", e) }
                }
                .addOnFailureListener {
                    pd.dismiss()
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { p0 ->
                    var progress: Double = (100.0 * p0.bytesTransferred / p0.totalByteCount)
                    pd.setMessage("${progress.toInt()}% uploaded")
                }

        } else {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
        }
    }

}