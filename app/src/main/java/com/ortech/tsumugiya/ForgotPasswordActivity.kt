package com.ortech.tsumugiya

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_forgot_password.*


class ForgotPasswordActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_forgot_password)

    buttonBackToLogin.setOnClickListener {
      finish()
    }

    buttonSendEmail.setOnClickListener {
      forgotPasswordAction()
    }
  }

  private fun forgotPasswordAction() {
    val auth = Firebase.auth
    val emailEditText = editTextForgotPasswordEmail
    val alert = AlertDialog.Builder(this)
    alert.setTitle(R.string.user_forgot_password)
    alert.setView(emailEditText)
    alert.setMessage("Enter Email")
    alert.setPositiveButton(getString(R.string.send), DialogInterface.OnClickListener() { dialogInterface : DialogInterface, _ ->
      val email = emailEditText.text.toString()
      auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {
            Toast.makeText(baseContext, "Email Sent", Toast.LENGTH_SHORT).show()
            finish()
            dialogInterface.dismiss()
          }
        }

    })

    alert.setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener() { dialogInterface: DialogInterface, _ ->
      dialogInterface.cancel()
    })

  }


}
