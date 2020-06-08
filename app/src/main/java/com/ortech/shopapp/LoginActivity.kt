package com.ortech.shopapp

import android.content.Context
import android.content.DialogInterface
import com.ortech.shopapp.Models.UserSingleton
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ortech.shopapp.Models.GlobalUser
import com.ortech.shopapp.Models.RequestCode
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*


class LoginActivity : AppCompatActivity(), View.OnClickListener{

  private lateinit var auth: FirebaseAuth
  private var callbackManager: CallbackManager? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    auth = Firebase.auth

//    if (BuildConfig.DEBUG) {
//      editTextLoginEmail.text = Editable.Factory.getInstance().newEditable("rhuet.transit@gmail.com")
//      editTextLoginPassword.text = Editable.Factory.getInstance().newEditable("ixoojb")
//    }

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
//          forgotPasswordAction()
          val intent = Intent(this, ForgotPasswordActivity::class.java)
          startActivity(intent)
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
      loginStaffAccount(email, password)
    }

  }

  private fun loginStaffAccount(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
      if (task.isSuccessful) {
        Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT)
          .show()
              val intent = Intent(baseContext, QRCodeScannerActivity::class.java)
          intent.putExtra(QRCodeScannerActivity.ARGS_TYPE, QRCodeScannerActivity.TYPE_STAFF)
          startActivity(intent)
      } else {
        Log.d(TAG, task.exception.toString())
        loginCustomerAccount(email, password)
      }
    }
      .addOnFailureListener {
        invalidLogin()
      }
  }

  private fun loginCustomerAccount(email: String, password: String) {
    startLoading()
    val db = Firebase.firestore
    db.collection("GlobalUsers")
      .whereEqualTo("email", email)
      .whereEqualTo("password", password)
      .limit(1)
      .get()
      .addOnSuccessListener {
        if (it.documents.isNotEmpty()) {
          Toast.makeText(this, "Successfully Login", Toast.LENGTH_SHORT)
            .show()
          val userID = it.first()["userID"] as String
          val name = it.first()["name"] as String
          UserSingleton.instance.name = name
          UserSingleton.instance.userID = userID
          checkUUID(LoginType.Email, userID)
          updateUserInfo(LoginType.Email)
//          val intent = Intent(baseContext, BottomNavigationActivity::class.java)
//          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//          startActivity(intent)
        } else {
          stopLoading()
          invalidLogin()
        }
      }
      .addOnFailureListener {
        stopLoading()
        invalidLogin()
      }
  }


  private fun forgotPasswordAction() {
    val emailEditText = EditText(this)
    val alert = AlertDialog.Builder(this)
    alert.setTitle(R.string.user_forgot_password)
    alert.setView(emailEditText)
    alert.setMessage("Enter Email")
    alert.setPositiveButton("Send", DialogInterface.OnClickListener() { dialogInterface : DialogInterface, _ ->
      val email = emailEditText.text.toString()
      auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            Toast.makeText(baseContext, "Email Sent", Toast.LENGTH_SHORT).show()
            dialogInterface.dismiss()
          }
        }

    })

    alert.setNegativeButton("Cancel", DialogInterface.OnClickListener() { dialogInterface: DialogInterface, _ ->
      dialogInterface.cancel()
    })

  }


  private fun loginUsingFacebook(){
    startLoading()
    val fbButton = buttonFacebookLoginMain
    callbackManager = CallbackManager.Factory.create();
    fbButton.setPermissions("email", "public_profile")

    fbButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
      override fun onSuccess(loginResult: LoginResult) {
        Log.d(TAG, "facebook:onSuccess:$loginResult")

//        handleFacebookAccessToken(loginResult.accessToken)
        val fbLogin = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
        Log.d(TAG, "FB Token: ${loginResult.accessToken.token} ${loginResult.accessToken.userId}")
        FirebaseAuth.getInstance().signInWithCredential(fbLogin).addOnCompleteListener {
          if (it.isSuccessful) {
            Log.d(TAG, "LoggedIn FB ${FirebaseAuth.getInstance().currentUser?.uid} ${FirebaseAuth.getInstance().currentUser?.displayName}")
//            FirebaseAuth.getInstance().signOut()
            UserSingleton.instance.name = Firebase.auth.currentUser?.displayName
            Firebase.auth.currentUser?.uid?.let { it1 -> checkUUID(LoginType.Facebook, it1) }
            updateUserInfo(LoginType.Facebook)
          } else {
            Log.d(TAG, "FB Failed to login")
            stopLoading()
          }
        }.addOnFailureListener{
          Log.d(TAG, "FB failure: ${it.localizedMessage}")
          stopLoading()
        }
      }

      override fun onCancel() {
        Log.d(TAG, "facebook:onCancel")
        stopLoading()
      }

      override fun onError(error: FacebookException) {
        Log.d(TAG, "facebook:onError", error)
        stopLoading()
      }
    })
    buttonFacebookLoginMain.performClick()

  }

  private fun loginUsingGoogle() {
    startLoading()
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
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
      val task = GoogleSignIn.getSignedInAccountFromIntent(data)
      Log.d(TAG, "Credentials: ${task.toString()}")
      Log.d(TAG, "Credentials: ${Firebase.auth.currentUser.toString()}")
      Log.d(TAG, "Credentials: ${FirebaseAuth.getInstance().currentUser?.uid}")

      GoogleSignIn.getSignedInAccountFromIntent(data)
      try {
        val account = task.getResult(ApiException::class.java)
        val cred = GoogleAuthProvider.getCredential(account.idToken, null)

        FirebaseAuth.getInstance().signInWithCredential(cred).addOnCompleteListener {
          if (it.isSuccessful) {
            Log.d(TAG, "Success Credentials: ${Firebase.auth.currentUser?.email}")
            UserSingleton.instance.name = Firebase.auth.currentUser?.displayName
            Firebase.auth.currentUser?.uid?.let { it1 -> checkUUID(LoginType.Google, it1) }
            updateUserInfo(LoginType.Google)
          }
        }
      } catch (e: ApiException) {
        stopLoading()
        invalidLogin()
      }

//      handleSignInResult(task)
    } else {
      callbackManager?.onActivityResult(requestCode, resultCode, data)
    }
  }

  private fun updateUserInfo(type: LoginType) {
    // replace singleton
    // replace userID
    // replace preference
    var userID = ""
    if (type != LoginType.Email) {
      userID = FirebaseAuth.getInstance().currentUser?.uid ?:return ?: return
    } else {
      userID = UserSingleton.instance.userID
    }
    UserSingleton.instance.userID = userID
    UserSingleton.instance.email = Firebase.auth.currentUser?.email

    val db = Firebase.firestore.collection("GlobalUsers")

    db.whereEqualTo("userID", userID).get()
      .addOnSuccessListener {
        if (it.count() != 0) {
          // get total points and set to singleton
          checkUUID(type, userID) // to replace exisitng user logged in
          getTotalPointsHistory()
        } else {
          // create new document for global users and totalpoints
          val newGlobalUser = GlobalUser(
            deviceUUID = userID,
            restoID = "qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP",
            restoName = "ラーメン世界",
            userID = userID,
            device = "android"
          )
          db.document(userID).set(newGlobalUser)
            .addOnSuccessListener {
              Log.d(TAG, "Added new global user")
              createTotalPointsData(userID)
            }
            .addOnFailureListener {
              Log.e(TAG, "Failed to add new global user ${it.toString()}")
            }
          checkUUID(type, userID)


        }
      }
  }

  private fun checkUUID(type: LoginType, userID: String) {
    var preferenceString: String = ""
    preferenceString = when (type) {
      LoginType.Facebook -> {
        getString(R.string.preference_UUID_key_facebook)
      }
      LoginType.Email -> {
        getString(R.string.preference_UUID_key_email)
      }
      LoginType.Google -> {
        getString(R.string.preference_UUID_key_google)
      }
    }
    val sharedPreferences = getSharedPreferences(
      preferenceString, Context.MODE_PRIVATE) ?: return

    with(sharedPreferences.edit()) {
      putString(preferenceString, userID)
      apply()
    }
  }

  private fun getTotalPointsHistory() {
    val userID = UserSingleton.instance.userID
    val db = Firebase.firestore
    db.collection("TotalPoints").whereEqualTo("userID", userID)
      .get()
      .addOnSuccessListener { querySnapshot ->
        // check date
        if (querySnapshot.count() != 0) {
          val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
          val today = sdf.format(Date()).toString()
          val timestamp = querySnapshot.first()["dates"] as Timestamp
          val timestampToday = sdf.format(timestamp.toDate()).toString()
          val currentPoints = querySnapshot.first()["totalPoints"] as Number

          UserSingleton.instance.setCurrentPoints(currentPoints.toInt())
          if (today == timestampToday) {
            Log.d(HomeScreen.TAG, "Dates are equal")
            val pointsToday = querySnapshot.first()["pointsToday"] as Number
            val lastPoints = querySnapshot.first()["lastPoints"] as Number
            UserSingleton.instance.setLastPointsTransferred(pointsToday.toInt())
            UserSingleton.instance.setPointsToday(lastPoints.toInt())
          } else {
            UserSingleton.instance.setLastPointsTransferred(0)
            UserSingleton.instance.setPointsToday(0)
          }
        }
        Firebase.auth.signOut()
        stopLoading()
        gotoHomeScreen()
      }
      .addOnFailureListener {
        stopLoading()
        UserSingleton.instance.setLastPointsTransferred(0)
        UserSingleton.instance.setPointsToday(0)
        UserSingleton.instance.setCurrentPoints(0)
      }
  }

  private fun createTotalPointsData(userID: String) {
    val db = Firebase.firestore
    db.collection("TotalPoints").add(
      hashMapOf(
        "userID" to userID,
        "totalPoints" to 0,
        "lastPoints" to 0,
        "pointsToday" to 0,
        "deviceUUID" to userID,
        "restoID" to "qORS5giJWx101ituzXveVZPqQENAh1hEriCRyeTP",
        "restoName" to "ラーメン世界",
        "dates" to Timestamp(Date())
      )
    ).addOnCompleteListener {
      Firebase.auth.signOut()
      gotoHomeScreen()
      stopLoading()
    }
  }

  private fun gotoHomeScreen() {
    val intent = Intent(baseContext, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)
  }

  private fun startLoading() {
    progressBarLogin.visibility = View.VISIBLE
  }

  private fun stopLoading() {
    progressBarLogin.visibility = View.INVISIBLE
  }

  private fun invalidLogin() {

    val builder = AlertDialog.Builder(applicationContext)
    builder.setTitle(resources.getString(R.string.options_login))
    builder.setMessage(resources.getString(R.string.invalid_login))
    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, which: Int ->
      dialog.dismiss()
    }))
  }

  companion object {
    const val TAG = "LoginActivity"

    enum class LoginType {
      Facebook, Google, Email
    }
  }
}

interface DialogListener {
  fun onPositiveClick()
  fun onNegativeClick()
}