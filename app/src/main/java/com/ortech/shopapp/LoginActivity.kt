package com.ortech.shopapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.gcm.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Models.RequestCode
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), View.OnClickListener{

  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    auth = Firebase.auth

    buttonLogin.setOnClickListener(this)
    buttonLoginLater.setOnClickListener(this)
    textViewLoginForgotPassword.setOnClickListener(this)
    buttonFacebookLogin.setOnClickListener(this)
    buttonGoogleLogin.setOnClickListener(this)
  }

  override fun onClick(v: View?) {
    v?.let {
      when(it.id) {
        R.id.buttonLoginLater -> { finish() }
        R.id.buttonLogin -> {
          loginUsingEmail()
        }
        R.id.textViewLoginForgotPassword -> {
          forgotPasswordAction()
        }
        R.id.buttonFacebookLogin -> {
          loginUsingFacebook()
        }
        R.id.buttonGoogleLogin -> {
          loginUsingGoogle()
        }
      }
    }
  }

  private fun loginUsingEmail() {
    val email = editTextLoginEmail.text.toString()
    val password = editTextLoginPassword.text.toString()
    var shouldLogin = true

    Log.d(TAG, "Trying to login")

    if (email.trim().isEmpty()) {
      shouldLogin = false
      Toast.makeText(baseContext, "Email is required",
        Toast.LENGTH_SHORT).show()
    }

    if (password.trim().isEmpty()) {
      shouldLogin = false
      Toast.makeText(baseContext, "Password is required",
        Toast.LENGTH_SHORT).show()
    }

    if (shouldLogin) {
      Log.d(TAG, "Checking details")
      auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
          val intent = Intent(this, BottomNavigationActivity::class.java)
          startActivity(intent)
          Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT)
            .show()
        } else {
          Log.d(TAG, task.exception.toString())
          Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT)
            .show()
        }
      }
        .addOnFailureListener {
          Log.e(TAG, it.localizedMessage!!)
        }
    }

  }

  private fun forgotPasswordAction() {
    val emailEditText = EditText(this)
    val alert = AlertDialog.Builder(this)
    alert.setTitle(R.string.user_forgot_password)
    alert.setView(emailEditText)
    alert.setMessage("Enter Email")

    alert.setPositiveButton("Send", DialogInterface.OnClickListener() { dialogInterface, _ ->
      val email = emailEditText.text.toString()
      auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            Toast.makeText(baseContext, "Email Sent", Toast.LENGTH_SHORT).show()
            dialogInterface.dismiss()
          }
        }

    })

    alert.setNegativeButton("Cancel", DialogInterface.OnClickListener() { dialogInterface, _ ->
      dialogInterface.cancel()
    })

  }


  private fun loginUsingFacebook(){}

  private fun loginUsingGoogle() {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(
              "AIzaSyD1GZtk6K-FlFK-40kvo-fLNJk_KyHdGKo")
      .requestEmail()
      .build()

    val mGoogleClient = GoogleSignIn.getClient(this, gso)
    val signInIntent = mGoogleClient.signInIntent
    startActivityForResult(signInIntent, RequestCode.GOOGLE)

  }

  private fun resendVerification(email: String) {
    val user = auth.currentUser
    user?.sendEmailVerification()
      ?.addOnCompleteListener { task ->
        if (task.isSuccessful) {
          Toast.makeText(this, "Email Verification Sent", Toast.LENGTH_SHORT)
            .show()
        }
      }
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
    if (requestCode == RequestCode.GOOGLE) { // The Task returned from this call is always completed, no need to attach
      Log.d(TAG, "Logged in with google")
// a listener.
//      val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
//      handleSignInResult(task)
    }

    if (requestCode == RequestCode.FACEBOOK) {
      Log.d(TAG, "Logged in with facebook")
    }
  }



  private fun login2() {
//    val db = Firebase.firestore
//    db.collection("GlobalUsers")
//      .whereEqualTo("email", email)
//      .whereEqualTo("password", password)
//      .limit(1)
//      .get()
//      .addOnSuccessListener {
//        if (it.documents.isNotEmpty()) {
//          // TODO Change text
//          Toast.makeText(this, "Successfully Login", Toast.LENGTH_SHORT)
//            .show()
//          // TODO add startActivity(toHomeScreen)
//        } else {
//          Toast.makeText(this, "Invalid User name password", Toast.LENGTH_SHORT)
//            .show()
//        }
//      }
//      .addOnFailureListener {
//        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT)
//          .show()
//      }
  }

  companion object {
    const val TAG = "LoginActivity"
  }
}

interface DialogListener {
  fun onPositiveClick()
  fun onNegativeClick()
}