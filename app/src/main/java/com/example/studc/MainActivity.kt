package com.example.studc

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.example.studc.ui.assignment.AssignmentFragment
import com.example.studc.ui.main.MainFragment
import com.example.studc.ui.studymaterial.ui.studymaterial.SubjectListFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    // for navigation
    lateinit var toggle: ActionBarDrawerToggle

    internal val ANONYMOUS = "Anonymous"
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null
    lateinit var db : FirebaseFirestore

    private lateinit var mFirebaseDatabase : FirebaseDatabase
    private lateinit var mDatabaseReference : DatabaseReference

    internal val RC_SIGN_IN = 1
    //val db = Firebase.firestore
    companion object{
        @JvmStatic
        lateinit var mUsername : String
        @JvmStatic
        var mAuthUid : String? = null
        @JvmStatic
        var mAuthEmail: String? = null
        @JvmStatic
        var mAuthUri : Uri? = null
        @JvmStatic
        var rollNo : String? = null
    }

    //supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true) alt+enter gave me this line below

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            if (FirebaseApp.getApps(this).size==0) {
                Firebase.database.setPersistenceEnabled(true)
            }
            navView.menu.getItem(0).setChecked(true)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        // navigation
        toggle = ActionBarDrawerToggle(this, drawerLayout,
            R.string.openNavDrawer, R.string.closeNavDrawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.notification_navMenu -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, MainFragment.newInstance())
                        .commitNow()
                }
                R.id.logout -> {
                    AuthUI.getInstance().signOut(this)
                }
                R.id.studyMaterial_navMenu -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, SubjectListFragment.newInstance())
                        .commitNow()
                }
                R.id.assignment_navMenu -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, AssignmentFragment.newInstance())
                        .commitNow()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        //firebase login
        mFirebaseAuth = FirebaseAuth.getInstance()
        mUsername = ANONYMOUS

        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                onSignedInInitialise(
                    firebaseUser.displayName,
                    firebaseUser.email,
                    firebaseUser.photoUrl,
                    firebaseUser.uid,
                    firebaseUser.phoneNumber
                )
                db = FirebaseFirestore.getInstance()
                val docRef = db.collection("UserDetails")
                    .document(firebaseUser.uid)
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Log.d("MainActivity", "DocumentSnapshot data: ${document.data}")
                            rollNo = document.data?.get("RollNo").toString()
                        } else {
                            Log.d("MainActivity", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("MainActivity", "get failed with ", exception)
                    }


            } else {
                onSignedOutCleanUp()
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(
                            Arrays.asList(
                                AuthUI.IdpConfig.GoogleBuilder().build(),
                                AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.PhoneBuilder().build()
                            )
                        )
                        .build(),
                    RC_SIGN_IN
                )
            }
        }
    }


    private fun setNavigationHeader(){
        // set navigation header
        var navigationView : NavigationView = findViewById(R.id.navView)
        var navHeader  = navigationView.getHeaderView(0)

        if (mUsername!=null) {
            navHeader.nav_header_name.setText(mUsername)
        }else {
            navHeader.nav_header_name.setText(ANONYMOUS)
        }

        if (mAuthEmail!=null) {
            navHeader.nav_header_email.setText(mAuthEmail)
        } else{
            mAuthEmail = "user@email.com"
            navHeader.nav_header_email.setText(mAuthEmail)
        }

        if (mAuthUri!=null) {
            Glide.with(this)
                .load(mAuthUri)
                .circleCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(navHeader.nav_header_image)
        } else{
            navHeader.nav_header_image.setImageResource(R.mipmap.ic_launcher_round)
            //auth.setTitle("Login")
        }
    }

    private fun onSignedOutCleanUp() {
        mUsername = ANONYMOUS
    }

    private fun onSignedInInitialise(
        displayName: String?,
        email: String?,
        photoUrl: Uri?,
        uid: String,
        phoneNumber: String?
    ) {
        if (displayName != null) {
            mUsername = displayName
            if (photoUrl!=null) {
                mAuthUri = photoUrl
            }
            if (email!=null) {
                mAuthEmail = email
            }else if(phoneNumber!=null){
                mAuthEmail = phoneNumber
            }
            mAuthUid = uid
            mFirebaseDatabase = FirebaseDatabase.getInstance()
            mDatabaseReference = mFirebaseDatabase.reference.child("Feed")
//            mDatabaseReference.child("blah")
//                .setValue(Post(postText = "check check"))

        } else {
            mUsername = ANONYMOUS
        }
        setNavigationHeader()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_CANCELED) {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mAuthStateListener?.let { mFirebaseAuth?.addAuthStateListener(it) }
    }

    override fun onPause() {
        super.onPause()
        mAuthStateListener?.let { mFirebaseAuth?.removeAuthStateListener(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
            return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}