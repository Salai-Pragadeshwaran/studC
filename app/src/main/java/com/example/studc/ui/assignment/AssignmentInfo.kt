package com.example.studc.ui.assignment

data class AssignmentInfo(
    var name: String = "assignment name",
    var deadline: Long = 0L ,
    var marks: String = "" ,
    var subject: String = "Subject name",
    var link: String,
    var extension: String
) {
}