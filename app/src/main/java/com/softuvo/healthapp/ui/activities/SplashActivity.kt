package com.softuvo.healthapp.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import com.softuvo.healthapp.R
import com.softuvo.healthapp.data.prefs.PreferenceHandler
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.popup_layout_.*

class SplashActivity : AppCompatActivity() {

    private val SPLASHDELAY: Long = 2500
    private var mDelayHandler: Handler? = null

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            if (PreferenceHandler.readBoolean(applicationContext, PreferenceHandler.IS_LOGGED_IN, false)) {
                startActivity(Intent(applicationContext, Dashboard::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            } else {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setScaleAnimation(iv_app_logo)
    }

    override fun onResume() {
        super.onResume()
        mDelayHandler = Handler()
        mDelayHandler!!.postDelayed(mRunnable, SPLASHDELAY)
    }

    public override fun onDestroy() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }

    private fun setScaleAnimation(view: View) {
        val anim = ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.duration = 900
        view.startAnimation(anim)
    }
}

