package com.ortech.tsumugiya

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.ortech.tsumugiya.Models.UserSingleton
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : AppCompatActivity() {

  private var name: EditText? = null
  private var email: EditText? = null
  private var password: EditText? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_up)
    name = editTextSignUpName
    email = editTextSignUpEmail
    password = editTextSignupPassword
    setupButtons()
  }

  private fun setupButtons() {
    buttonSignUp.setOnClickListener {
      // TODO check fields
      //
      startLoading()
      val name = this.name?.text.toString()
      val email  = this.email?.text.toString()
      val password = this.password?.text.toString()
      var shouldContinue = true

      if (name.trim().isEmpty()) {
        Toast.makeText(applicationContext, "入力が必要です", Toast.LENGTH_SHORT).show()
        shouldContinue = false
        stopLoading()
      }

      if (email.trim().isEmpty()) {
        Toast.makeText(applicationContext, "入力が必要です", Toast.LENGTH_SHORT).show()
        shouldContinue = false
        stopLoading()
      }

      if (password.trim().isEmpty()) {
        Toast.makeText(applicationContext, "入力が必要です", Toast.LENGTH_SHORT).show()
        shouldContinue = false
        stopLoading()
      }


      if (shouldContinue) {
//        updateDocument(name, email, password)

        writeToDatabase(name, email, password)
      }
    }

    buttonSignUpLater.setOnClickListener {
      finish()
    }
  }

  private fun createUser(name: String, email: String, password: String) {
    val db = Firebase.firestore
    val userRef =  db.collection("GlobalUsers")
    val userID = UUID.randomUUID().toString()

    userRef.whereEqualTo("email", email)
      .get()
      .addOnSuccessListener {
        if (it.count() != 0) {
//          val builder = AlertDialog.Builder(applicationContext)
//          builder.setTitle(R.string.options_signup)
//          builder.setMessage("User is already Registered")
//          builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, which: Int ->
//            dialog.dismiss()
//          }))
//          builder.show()
          Toast.makeText(baseContext, "取得したユーザー名", Toast.LENGTH_SHORT).show()

        } else {
          userRef.add(hashMapOf(
            "email" to email,
            "password" to password,
            "name" to name,
            "userID" to userID,
            "deviceUUID" to userID,
            "restoID" to UserSingleton.instance.mainID,
            "restoName" to UserSingleton.instance.appName
          )).addOnSuccessListener {
            // update user data
            // update fcm token
            // update uuid
//            checkUUID(userID)
            sendEmailVerification()
            createTotalPointsData(userID)
          }
        }
      }
      .addOnFailureListener {
        stopLoading()
      }
  }

  private fun sendEmailVerification() {
    FirebaseAuth.getInstance().currentUser?.sendEmailVerification()?.addOnSuccessListener {
      FirebaseAuth.getInstance().signOut()
      stopLoading()
    }?.addOnFailureListener {
      FirebaseAuth.getInstance().signOut()
      stopLoading()
    }
  }

  private fun writeToDatabase(name: String, email: String, password: String) {
    val db = Firebase.firestore
    val userRef =  db.collection("GlobalUsers")

   Firebase.auth.createUserWithEmailAndPassword(email, password)
     .addOnCompleteListener {task ->
       if (task.isSuccessful) {

         userRef.whereEqualTo("email", email).get()
           .addOnSuccessListener {
             if (it.documents.size == 0) {
               createUser(name, email, password)
             } else {
               // user is alreadu registered
//               MaterialAlertDialogBuilder(this)
//                 .setTitle(R.string.options_signup)
//                 .setMessage("User is already Registered")
//                 .setPositiveButton("OK"){ dialog, which ->
//
//                 }.show()
             }
             stopLoading()
           }
           .addOnFailureListener {
             MaterialAlertDialogBuilder(this)
               .setTitle(R.string.options_signup)
               .setMessage(it.localizedMessage)
               .setPositiveButton("OK"){ dialog, which ->

               }.show()
             stopLoading()
           }
       } else {
         Log.d(TAG, "createUserWithEmail:failure", task.exception)
         Toast.makeText(baseContext, task.exception.toString(),
           Toast.LENGTH_SHORT).show()
         stopLoading()
       }
     }
     .addOnFailureListener {
       Log.e(TAG, "createUserWithEmail:failure", it)
       Toast.makeText(baseContext, "Authentication failed. ${it.toString()}",
         Toast.LENGTH_SHORT).show()
       stopLoading()
     }
  }

  private fun createTotalPointsData(userID: String) {
    startLoading()
    val db = Firebase.firestore
    db.collection("TotalPoints").add(
      hashMapOf(
        "userID" to userID,
        "totalPoints" to 0,
        "lastPoints" to 0,
        "pointsToday" to 0,
        "deviceUUID" to userID,
        "restoID" to UserSingleton.instance.mainID,
        "restoName" to UserSingleton.instance.appName,
        "dates" to Timestamp(Date()),
        "status" to "new"
      )
    ).addOnCompleteListener {

//      val builder = AlertDialog.Builder(applicationContext)
//      builder.setTitle(R.string.options_signup)
//      builder.setMessage("SignUp Successful")
//      builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, which: Int ->

//      }))
      stopLoading()
//            Toast.makeText(baseContext, "Sign Up Successful", Toast.LENGTH_SHORT).show()
//              val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
//        dialog.dismiss()


    }
      .addOnFailureListener {
        stopLoading()
      }
  }

  private fun updateDocument(name: String, email: String, password: String) {
    val db = Firebase.firestore
    val userRef =  db.collection("GlobalUsers")
    userRef.whereEqualTo("userID", UserSingleton.instance.userID)
      .get()
      .addOnSuccessListener {
        val document = it.documents.first()
        userRef.document(document.id)
          .set(hashMapOf(
            "name" to name,
            "email" to email,
            "password" to password
          ), SetOptions.merge())
          .addOnSuccessListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.options_signup)
            builder.setMessage("SignUp Successful")
            builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = { dialog: DialogInterface, which: Int ->

              dialog.dismiss()
            }))
          }
      }.addOnFailureListener {
      }
  }

  private fun startLoading() {
    progressBarSignUp.visibility = View.VISIBLE
  }

  private fun stopLoading() {
    progressBarSignUp.visibility = View.INVISIBLE
  }

  companion object {
    const val TAG = "SignUpActivity"
  }

}
