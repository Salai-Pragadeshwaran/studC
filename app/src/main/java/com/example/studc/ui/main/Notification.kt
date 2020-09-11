package com.example.studc.ui.main

import android.provider.Settings
import com.example.studc.MainActivity

data class Notification (
    var imgUrl: String = MainActivity.mAuthUri.toString(),
    var username: String = MainActivity.mUsername,
    var postText: String? = null,
    var userUid: String? = MainActivity.mAuthUid,
    var postTime: Long = System.currentTimeMillis()) {
}