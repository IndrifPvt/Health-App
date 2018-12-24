package com.softuvo.healthapp.ui.activities

import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.text.TextUtils
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.softuvo.healthapp.R
import com.softuvo.healthapp.core.BaseActivity
import com.softuvo.utils.Validations
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : BaseActivity() {
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        var textView = TextView(this);
        textView.setText(title);
        textView.setTextSize(20F);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams( LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.colorWhite));
        supportActionBar!!.displayOptions = android.app.ActionBar.DISPLAY_SHOW_CUSTOM or android.app.ActionBar.DISPLAY_HOME_AS_UP
        getSupportActionBar()!!.setCustomView(textView);

        auth=FirebaseAuth.getInstance()
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_forgot -> {
                forgotpassword()
            }
        }
    }

    private fun forgotpassword() {
        val email = et_forgot_email.getText().toString().trim({ it <= ' ' })

        if (!Validations.isValidEmail(email)) {
            Toast.makeText(application, R.string.err_msg_email_address, Toast.LENGTH_SHORT).show()
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Toast.makeText(
                        this@ForgotPassword,
                        R.string.pwd_email_sent,
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                } else {
                    Toast.makeText(this@ForgotPassword, R.string.Failed_email, Toast.LENGTH_SHORT).show()
                }

            }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.getItemId() === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item)
    }
}
