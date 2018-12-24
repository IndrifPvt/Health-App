package com.softuvo.healthapp.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.softuvo.healthapp.R
import com.softuvo.healthapp.core.BaseActivity
import com.softuvo.healthapp.data.prefs.PreferenceHandler
import com.softuvo.healthapp.model.User
import com.softuvo.utils.Validations

class LoginActivity : BaseActivity() {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> {
                LoginStudent()
            }
            R.id.tv_register -> {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
            R.id.tv_forgot -> {
                startActivity(Intent(applicationContext, ForgotPassword::class.java))
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
        }
    }

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var fgt_email: EditText? = null
    private var auth: FirebaseAuth? = null
    private var btnLogin: Button? = null
    private var fgt_pwd: Button? = null
    lateinit var jLoginDatabase: DatabaseReference
    lateinit var useref: DatabaseReference
    lateinit var db1: DatabaseReference
    lateinit var pwforgot: PopupWindow
    lateinit var forgot: TextView
    private val TAG = "MyActivity"
    lateinit var progressBar: ProgressDialog
    private val EMAIL = "email"
    //lateinit var session: SessionManager
    lateinit var ProFname: String
    internal var ProLname: String? = null
    lateinit var ProEmail: String
    internal var ProStream: String? = null
    internal var ProDob = ""
    internal var ProMob = ""
    internal var Gender = ""
    internal var ProImg = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
      //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
      /*  progressBar = ProgressDialog(this)
        progressBar.setMessage("Logging You in")
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressBar.isIndeterminate = true
        progressBar.setCancelable(false)*/
        inputEmail = findViewById<View>(R.id.et_user_email_login) as EditText
        inputPassword = findViewById<View>(R.id.et_user_password_login) as EditText
        btnLogin = findViewById<View>(R.id.btn_login) as Button
        auth = FirebaseAuth.getInstance()

        // session = SessionManager(applicationContext)
        useref = FirebaseDatabase.getInstance().reference
        db1 = useref.child("User")
    }


    fun LoginStudent() {
        val email = inputEmail!!.text.toString().trim { it <= ' ' }
        val password = inputPassword!!.getText().toString().trim({ it <= ' ' })
        if (!Validations.isValidEmail(email)) {
            Toast.makeText(applicationContext, R.string.err_msg_email_address,Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, R.string.enter_password, Toast.LENGTH_SHORT).show()
            return
        }
        val EmailQuery = db1.orderByChild("email").equalTo(email)
        EmailQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var user: User? = null
                if (dataSnapshot.exists()) {
                    for (d in dataSnapshot.children) {
                        user = d.getValue(User::class.java)
                    }
                    ProFname = user!!.username
                    ProEmail = user!!.useremail

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@LoginActivity,
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {

                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val emailid = currentUser!!.email!!.replace(".", ",")

                        jLoginDatabase = FirebaseDatabase.getInstance().reference.child("User").child(emailid)
                        jLoginDatabase.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val userType = dataSnapshot.child("usertype").value as String?
                                        checkIfEmailVerified()

                            }

                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        })

                    } else {
                        Toast.makeText(applicationContext, "Wrong Crendentials", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun checkIfEmailVerified() {
        val users = FirebaseAuth.getInstance().currentUser
        val emailVerified = users!!.isEmailVerified
        if (!emailVerified) {
            Toast.makeText(this, R.string.verify_email, Toast.LENGTH_SHORT).show()
            auth!!.signOut()

        } else {
            val intentstudent = Intent(this@LoginActivity, Dashboard::class.java)
            PreferenceHandler.writeBoolean(applicationContext, PreferenceHandler.IS_LOGGED_IN, true)
            intentstudent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intentstudent)
            finish()
        }
    }

}