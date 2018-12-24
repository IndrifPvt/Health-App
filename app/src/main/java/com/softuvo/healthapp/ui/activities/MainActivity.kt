package com.softuvo.healthapp.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.softuvo.healthapp.R
import com.softuvo.healthapp.R.id.all
import com.softuvo.healthapp.core.BaseActivity
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener
import com.softuvo.healthapp.data.prefs.PreferenceHandler
import io.reactivex.internal.util.HalfSerializer.onComplete


class MainActivity : BaseActivity() {
    lateinit var auth: FirebaseAuth
    private lateinit var authStateListener:FirebaseAuth.AuthStateListener
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_bmr -> {
                val intent = Intent(this@MainActivity, BmrCalculator::class.java)
                startActivity(intent)

            }

            R.id.btn_bmi -> {
                val intent = Intent(this@MainActivity, BMICalculator::class.java)
                startActivity(intent)

            }
            R.id.btn_excersise -> {
                val intent = Intent(this@MainActivity, ExcersiseBurn::class.java)
                startActivity(intent)

            }
            R.id.btn_calculate_calorie -> {
                val intent = Intent(this@MainActivity, CaloriesCounter::class.java)
                startActivity(intent)

            }
            R.id.btn_calculate_steps -> {
                val intent = Intent(this@MainActivity, StepCounter::class.java)
                startActivity(intent)

            }
            R.id.btn_logout -> {
               signout()

            }
            R.id.btn_calculate_pulse-> {
                val intent = Intent(this@MainActivity, PulseCounter::class.java)
                startActivity(intent)
            }
            R.id.iv_profile-> {
                val intent = Intent(this@MainActivity, UserProfile::class.java)
                startActivity(intent)
            }
        }
    }

    private fun signout() {
        auth.signOut()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_login)
        auth = FirebaseAuth.getInstance()
         authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                PreferenceHandler.writeBoolean(applicationContext, PreferenceHandler.IS_LOGGED_IN, false)
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        auth.addAuthStateListener(authStateListener);
    }
}

