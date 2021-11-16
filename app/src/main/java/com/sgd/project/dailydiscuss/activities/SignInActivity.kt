package com.sgd.project.dailydiscuss.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.ui.email.TroubleSigningInFragment.TAG
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sgd.project.dailydiscuss.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.sgd.project.dailydiscuss.daos.UserDao
import com.sgd.project.dailydiscuss.fragments.AdminLoginFragment
import com.sgd.project.dailydiscuss.model.User

class SignInActivity : AppCompatActivity() {
    private val RC_SIGN_IN: Int= 199
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInButton: SignInButton
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var adminButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInButton= findViewById(R.id.signInButton)
        progressBar= findViewById(R.id.progressBar)
        adminButton= findViewById(R.id.adminLoginButton)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        signInButton.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()

        //To check whether user is already signed in or not
        val user= auth.currentUser
        updateUI(user)
    }

    private fun signIn() {
        //signInIntent is the intent that is opened to ask the user which google signed in profile does
        //they want to select to sign in this app
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            //Fetch the signed in account from the task
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            //Authenticate the user in the firebase
            firebaseAuthWithGoogle(account.idToken!!)
        }
        catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed"+ e.statusCode)
        }
    }

    /*After user successfully signs in with google account, use the idToken to create a account
    for the user in your app and authenticate it with firebase
    */
    private fun firebaseAuthWithGoogle(idToken: String) {
        //Get the credential of the user from the idToken
        val credential= GoogleAuthProvider.getCredential(idToken, null)

        //To show Progress bar while user is signing in using google credentials
        signInButton.visibility= View.GONE
        progressBar.visibility= View.VISIBLE

        //Perform signing in task in background task
        GlobalScope.launch(Dispatchers.IO){
            val auth= auth.signInWithCredential(credential).await()
            val firebaseUser= auth.user
            //Transferring the task of updating the ui to main thread from background thread
            withContext(Dispatchers.Main){
                updateUI(firebaseUser)
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null){

            //Taking user details from google account
            val user= User(firebaseUser.uid, firebaseUser.displayName!!, firebaseUser.photoUrl.toString())
            UserDao().addUser(user)     //Adding user details in our database

            val mainActivityIntent= Intent(this, TopicActivity::class.java)
            mainActivityIntent.putExtra("flag", "false")
            startActivity(mainActivityIntent)
            finish()
        }
        else{
            //If signing in fails, show the signin button again
            signInButton.visibility= View.VISIBLE
            progressBar.visibility= View.GONE
        }
    }

    //For Admin Login
    fun adminLogin(view: View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, AdminLoginFragment())
            .addToBackStack(null)
            .commit()

        signInButton.visibility= View.GONE
        adminButton.visibility= View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        signInButton.visibility= View.VISIBLE
        adminButton.visibility= View.VISIBLE
    }
}