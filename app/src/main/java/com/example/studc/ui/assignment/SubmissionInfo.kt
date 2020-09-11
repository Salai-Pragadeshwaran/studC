package com.example.studc.ui.assignment

data class SubmissionInfo(
    var submissionTime: Long = System.currentTimeMillis(),
    var deadlineMet: Boolean = true
) {
}