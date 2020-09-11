package com.example.studc.ui.studymaterial.ui.studymaterial

import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.study_material_item.view.*

class StudyMaterialAdapter(val studyMaterials: java.util.ArrayList<StudyMaterial>, private val mcontext: Context, var myClipboard: ClipboardManager)
    : RecyclerView.Adapter<StudyMaterialAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        internal var nameTextView: TextView
        internal var linkTextView: Button
        internal var downloadButton: Button

        init {
            nameTextView = itemView.materialName
            linkTextView = itemView.copyMaterialLink
            downloadButton = itemView.downloadMaterial
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(com.example.studc.R.layout.study_material_item, parent, false)
        return StudyMaterialAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return studyMaterials.size
    }

    override fun onBindViewHolder(holder: StudyMaterialAdapter.ViewHolder, position: Int) {
        holder.nameTextView.setText(studyMaterials[position].name)
        holder.linkTextView.setOnClickListener {
            val text = studyMaterials[position].link
            var myClip = ClipData.newPlainText("text", text)
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(mcontext, "Link Copied", Toast.LENGTH_SHORT).show()
        }
        holder.downloadButton.setOnClickListener {
            downloadFile(mcontext, studyMaterials[position].name, studyMaterials[position].extension
                ,DIRECTORY_DOWNLOADS, studyMaterials[position].link)
        }
    }

    fun downloadFile(context: Context, fileName: String, fileExtension: String,
                     destinationDirectory: String, url: String){
        var downloadManager :DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        var uri: Uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension)

        downloadManager.enqueue(request)
        Toast.makeText(mcontext, "Starting Download $fileName$fileExtension", Toast.LENGTH_SHORT).show()
    }


}
