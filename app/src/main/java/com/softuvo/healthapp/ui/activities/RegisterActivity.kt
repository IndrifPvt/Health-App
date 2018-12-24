package com.softuvo.healthapp.ui.activities

import android.app.ActionBar
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.softuvo.healthapp.R
import com.softuvo.healthapp.core.BaseActivity
import com.softuvo.healthapp.model.User
import com.softuvo.utils.Validations
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity :  BaseActivity() {
    private var name=""
    private var email=""
    private var password=""
    private var phone=""
    private var location=""
    private var age=""
    private var height_feet=""
    private var height_inch=""
    private var weight=""
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_register -> {
                signup()
            }
        }
    }

    lateinit var db: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        var textView = TextView(this);
        textView.setText(title);
        textView.setTextSize(20F);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams( LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.colorWhite));
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM or ActionBar.DISPLAY_HOME_AS_UP
        getSupportActionBar()!!.setCustomView(textView);
        db = FirebaseDatabase.getInstance().getReference("User")
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
    }
    private fun signup() {
         name = et_user_name.getText().toString().trim()
         email = et_user_email.getText().toString().trim()
         password = et_user_password.getText().toString().trim()
         phone=et_user_phone.text.toString().trim()
         location=et_user_location.text.toString().trim()
         age=et_user_age.text.toString().trim()
         height_feet=et_user_height_feet.text.toString().trim()
         height_inch=et_user_height_inch.text.toString().trim()
         weight=et_user_weight.text.toString().trim()


        if (TextUtils.isEmpty(name)) {
            Toast.makeText(applicationContext, R.string.enter_name, Toast.LENGTH_SHORT).show()
            return
        }

        if (!Validations.isValidEmail(email)) {
            Toast.makeText(applicationContext, R.string.err_msg_email_address, Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, R.string.enter_password, Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(applicationContext, R.string.enter_mobile, Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(location)) {
            Toast.makeText(applicationContext, R.string.enter_location, Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(age)) {
            Toast.makeText(applicationContext, R.string.enter_age, Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(height_feet)) {
            Toast.makeText(applicationContext, R.string.correct_height, Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(height_inch)) {
            Toast.makeText(applicationContext, R.string.correct_height, Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(weight)) {
            Toast.makeText(applicationContext, R.string.correct_weight, Toast.LENGTH_SHORT).show()
            return
        }
        createuser()
    }

    private fun createuser() {



        val id = db.push().key
        val usr = User(id!!, name, email,phone,location,age,height_feet,height_inch,weight)
        val user = auth.currentUser
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@RegisterActivity,
                OnCompleteListener<AuthResult> { task ->
                    Toast.makeText(
                        this@RegisterActivity,
                        "User Created" + task.isSuccessful,
                        Toast.LENGTH_SHORT
                    ).show()

                    if (!task.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity, "Authentication failed." + task.exception!!,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        db.child(email.replace(".", ",")).setValue(usr)
                        sendEmailVerification()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                })
    }


    private fun sendEmailVerification() {

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener(object : OnCompleteListener<Void> {
                val TAG = "MyActivity"

                override fun onComplete(task: Task<Void>) {
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity, R.string.verify_email_sent,
                            Toast.LENGTH_SHORT
                        ).show()
                        FirebaseAuth.getInstance().signOut()
                    }
                }
            })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.getItemId() === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item)
    }
}
