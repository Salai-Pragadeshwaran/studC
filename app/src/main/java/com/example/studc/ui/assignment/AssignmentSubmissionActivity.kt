package com.example.studc.ui.assignment

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studc.MainActivity
import com.example.studc.R
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_assignment_submission.*
import java.text.SimpleDateFormat
import java.util.*


class AssignmentSubmissionActivity : AppCompatActivity() {
    lateinit var subject: String
    lateinit var name: String
    lateinit var extension: String
    lateinit var link: String
    var selectedFile: Uri? = null
    private lateinit var mFirebaseDatabase : FirebaseDatabase
    private lateinit var mDatabaseReference : DatabaseReference
    private val mStorageRef: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_submission)
        subject = intent.getStringExtra("SUB_CODE")!!
        name = intent.getStringExtra("NAME")!!
        extension = intent.getStringExtra("EXTENSION")!!
        link = intent.getStringExtra("LINK")!!

        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase.reference.child("Assignments").child("$subject $name")

        //check assignment submission status
        mDatabaseReference.child(MainActivity.rollNo.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("submissionTime").value != null) {
                    var subTime =  formatTime(dataSnapshot.child("submissionTime").value!!.toString().toLong())
                    assignmentSubmissionStatus.text = "Assignment submitted on " + subTime
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        assignmentName.text = subject + " " + name

        downloadAssignment.setOnClickListener {
            downloadFile(
                this, assignmentName.text.toString(), extension
                , Environment.DIRECTORY_DOWNLOADS, link
            )
        }

        selectAssignment.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
        }

        uploadAssignment.setOnClickListener {
            if (MainActivity.rollNo!=null) {
                uploadFile()
            }
            else{
                Toast.makeText(this, "Access denied - You are not Authorised", Toast.LENGTH_SHORT).show()
            }
        }

        copyAssignmentLink.setOnClickListener {
                var myClipboard =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val text = link
                var myClip = ClipData.newPlainText("text", text)
                myClipboard.setPrimaryClip(myClip)
                Toast.makeText(applicationContext, "Link Copied", Toast.LENGTH_SHORT).show()

        }
    }

    private fun formatTime(time: Long): String {
        var cal : Calendar = Calendar.getInstance(Locale.ENGLISH)
        var tz: TimeZone = TimeZone.getDefault()
        cal.timeInMillis= time
        var sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy\nHH:mm:ss", Locale.getDefault())
        return sdf.format(cal.timeInMillis).toString()
    }

    private fun uploadFile() {
        if(selectedFile!=null){
            var pd = ProgressDialog(this)
            pd.setTitle("Uploading")
            pd.show()

            var docRef = FirebaseStorage.getInstance().
                                reference.child("Assignments/$subject $name/${MainActivity.rollNo} $subject $name")
            docRef.putFile(selectedFile!!)
                .addOnSuccessListener {
                    pd.dismiss()
                    Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show()
                    mDatabaseReference.child(MainActivity.rollNo.toString()).setValue(SubmissionInfo())
                }
                .addOnFailureListener{
                    pd.dismiss()
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { p0 ->
                    var progress: Double = (100.0 * p0.bytesTransferred/p0.totalByteCount)
                    pd.setMessage("${progress.toInt()}% uploaded")
                }

        }else{
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show()
        }
    }

    fun downloadFile(
        context: Context, fileName: String, fileExtension: String,
        destinationDirectory: String, url: String
    ) {
        var downloadManager: DownloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        var uri: Uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(
            context,
            destinationDirectory,
            fileName + fileExtension
        )

        downloadManager.enqueue(request)
        Toast.makeText(this, "Starting Download $subject $name$extension", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            if (data?.data!==null) {
                selectedFile = data?.data!!
                fileSelected.text = "Location of selected File - $selectedFile"
            }

        }
    }
}