package com.ortech.tsumugiya

import android.content.Context
import android.content.DialogInterface
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
import com.ortech.tsumugiya.Models.*
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*


class LoginActivity : AppCompatActivity(), View.OnClickListener{

  private lateinit var auth: FirebaseAuth
  private var callbackManager: CallbackManager? = null
  val db = Firebase.firestore

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    auth = Firebase.auth

    if (BuildConfig.DEBUG) {
      editTextLoginEmail.text = Editable.Factory.getInstance().newEditable("micoinfante1997+20@gmail.com")
//      editTextLoginPassword.text = Editable.Factory.getInstance().newEditable("ixoojb")
      editTextLoginPassword.text = Editable.Factory.getInstance().newEditable("test123")
    }

    buttonLogin.setOnClickListener(this)
    buttonLoginLater.setOnClickListener(this)
    textViewLoginForgotPassword.setOnClickListener(this)
    buttonFacebookLogin.setOnClickListener(this)
    buttonGoogleLogin.setOnClickListener(this)


   UserSingleton.instance.oldUserID =  UserSingleton.instance.userID
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

    startLoading()

    if (email.trim().isEmpty()) {
      shouldLogin = false
      Toast.makeText(baseContext, "入力が必要です",
        Toast.LENGTH_SHORT).show()
      stopLoading()
    }

    if (password.trim().isEmpty()) {
      shouldLogin = false
      Toast.makeText(baseContext, "入力が必要です",
        Toast.LENGTH_SHORT).show()
      stopLoading()
    }

    if (shouldLogin) {
      Log.d(TAG, "Checking details")
      loginStaffAccount(email, password)
    }

  }

  private fun loginStaffAccount(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
      if (task.isSuccessful) {

        FirebaseAuth.getInstance().currentUser?.let {
          if (it.isEmailVerified) {
            getStaffAccountDetails(email, password)
          } else {
            sendEmailVerification()
          }
        }
      } else {
        Log.d(TAG, task.exception.toString())
        stopLoading()
      }
    }
      .addOnFailureListener {
        invalidLogin()
        stopLoading()
      }
  }

  private fun sendEmailVerification() {
    stopLoading()
    FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.addOnSuccessListener {
      FirebaseAuth.getInstance().signOut()
    }?.addOnFailureListener {
      Log.d(TAG, "Send email verificationfailed: ${it.localizedMessage}")
      FirebaseAuth.getInstance().signOut()
    }

    val builder = AlertDialog.Builder(this)
    builder.setTitle("催促状")
    builder.setMessage("まずアカウントを確認してください。")
    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, which: Int ->
      FirebaseAuth.getInstance().signOut()
      dialog.dismiss()
    }))
    builder.show()
  }


  private fun getStaffAccountDetails(email: String, password: String) {
    val staffID = FirebaseAuth.getInstance().currentUser?.uid ?: return
    db.collection("CMSStaff")
      .whereEqualTo("userID", staffID)
      .get()
      .addOnSuccessListener {
        if (it.documents.count() != 0) {
          val staffData = it.documents.first().toObject(StaffAccount::class.java)
          StaffSingleton.instance.currentStaff = staffData
          Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT)
            .show()
          stopLoading()
          val intent = Intent(baseContext, QRCodeScannerActivity::class.java)
          intent.putExtra(QRCodeScannerActivity.ARGS_TYPE, QRCodeScannerActivity.TYPE_STAFF)
          startActivity(intent)
        } else {
          FirebaseAuth.getInstance().signOut()
          loginCustomerAccount(email, password)
        }
      }

  }

  private fun loginCustomerAccount(email: String, password: String) {
    startLoading()

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

          checkUUID(LoginType.Email, userID)
          updateUserInfo(LoginType.Email, userID)
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

  private fun updateUserInfo(type: LoginType, currentUserID: String? = "") {
    var userID = ""
    if (type != LoginType.Email) {
      userID = FirebaseAuth.getInstance().currentUser?.uid ?:return ?: return
    } else {
      if (currentUserID != null) {
        userID = currentUserID
      }
    }
    UserSingleton.instance.userID = userID
    UserSingleton.instance.email = Firebase.auth.currentUser?.email

    val userRef = db.collection("GlobalUsers")

    userRef.whereEqualTo("userID", userID).get()
      .addOnSuccessListener {
        if (it.count() != 0) {
          // get total points and set to singleton
          checkUUID(type, userID) // to replace exisitng user logged in
          getTotalPointsHistory()
          UserSingleton.instance.userID = userID
        } else {
          // create new document for global users and totalpoints
          val newGlobalUser = GlobalUser(
            deviceUUID = userID,
            restoID = UserSingleton.instance.mainID,
            restoName = UserSingleton.instance.appName,
            userID = userID,
            device = "android"
          )
          db.document(userID).set(newGlobalUser)
            .addOnSuccessListener {
              Log.d(TAG, "Added new global user")
              createTotalPointsData(userID)
              UserSingleton.instance.userID = userID
            }
            .addOnFailureListener {
              Log.e(TAG, "Failed to add new global user ${it.toString()}")
              stopLoading()
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
          val status = querySnapshot.first().get("status") as? String

          if (status != null) {
            // if status is new, it means it is a new account and not yet binded
            // transfer current points to the logged in account
            Log.d(TAG, "Status is not null")
            if ((status as String) == "new") {
              val toTransferPoints = UserSingleton.instance.getCurrentPoints()
              Log.d(TAG, "Transferring $toTransferPoints from " +
                      "${UserSingleton.instance.userID}:DOCID[${querySnapshot.first().id}] to" +
                      "$userID" +
                      "OLD: ${UserSingleton.instance.oldUserID}")
              UserSingleton.instance.transferPointsTo(querySnapshot.first().id, toTransferPoints)
              UserSingleton.instance.userID = userID
            }
          } else  {
            // if account is already binded
            UserSingleton.instance.userID = userID
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
          stopLoading()
        } // END: Null check for document
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
    db.collection("TotalPoints").add(
      hashMapOf(
        "userID" to userID,
        "totalPoints" to 0,
        "lastPoints" to 0,
        "pointsToday" to 0,
        "deviceUUID" to userID,
        "restoID" to UserSingleton.instance.appName,
        "restoName" to UserSingleton.instance.appName,
        "dates" to Timestamp(Date()),
        "status" to "new"
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

    val builder = AlertDialog.Builder(this)
    builder.setTitle(resources.getString(R.string.options_login))
    builder.setMessage(resources.getString(R.string.invalid_login))
    builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, which: Int ->
      stopLoading()
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