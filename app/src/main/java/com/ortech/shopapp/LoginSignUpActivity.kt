package com.ortech.shopapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_login_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class LoginSignUpActivity : AppCompatActivity(), View.OnClickListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login_sign_up)

    buttonOptionSignUp.setOnClickListener(this)
    buttonOptionLogin.setOnClickListener(this)
    buttonOptionLater.setOnClickListener(this)
  }

  override fun onClick(v: View?) {
    if (v != null) {
      when(v.id) {
        R.id.buttonOptionLater -> {
          finish()
        }
        R.id.buttonOptionSignUp -> {
          val intent = Intent(this, SignUpActivity::class.java)
          startActivity(intent)
        }
        R.id.buttonLogin -> {
          val intent = Intent(this, LoginActivity::class.java)
          startActivity(intent)
        }
      }
    }
  }


}
