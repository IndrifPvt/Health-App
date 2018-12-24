package com.softuvo.healthapp.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.softuvo.healthapp.R
import com.softuvo.healthapp.model.UserBmr
import com.softuvo.healthapp.model.UserPulse
import com.softuvo.utils.CommonUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_pulse_counter.*
import net.kibotu.heartrateometer.HeartRateOmeter
import net.kibotu.kalmanrx.jama.Matrix
import net.kibotu.kalmanrx.jkalman.JKalman

class PulseCounter : AppCompatActivity() {

    var counter:Int?=0
    var subscription: CompositeDisposable? = null
    var cdt: CountDownTimer?=null
    lateinit var Db: DatabaseReference
    lateinit var Auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pulse_counter)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
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
        Db = FirebaseDatabase.getInstance().getReference("Pulse")
        Auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
    }

    private fun startWithPermissionCheck() {
        if (!hasPermission(Manifest.permission.CAMERA)) {
            checkPermissions(REQUEST_CAMERA_PERMISSION, Manifest.permission.CAMERA)
            return
        }

        val kalman = JKalman(2, 1)

        // measurement [x]
        val m = Matrix(1, 1)

        // transitions for x, dx
        val tr = arrayOf(doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0))
        kalman.transition_matrix = Matrix(tr)

        // 1s somewhere?
        kalman.error_cov_post = kalman.error_cov_post.identity()

        val bpmUpdates = HeartRateOmeter()
            .withAverageAfterSeconds(3)
            .setFingerDetectionListener(this::onFingerChange)
            .bpmUpdates(preview)
            .subscribe({

                if (it.value == 0)
                    return@subscribe
                m.set(0, 0, it.value.toDouble())
                // state [x, dx]
                val s = kalman.Predict()
                // corrected state [x, dx]
                val c = kalman.Correct(m)
                var bpm = it.copy(value = c.get(0, 0).toInt())
                Log.v("HeartRateOmeter", "[onBpm] ${it.value} => ${bpm.value}")
                onBpm(bpm)
            }, Throwable::printStackTrace)

        subscription?.add(bpmUpdates)
    }

    @SuppressLint("SetTextI18n")
    private fun onBpm(bpm: HeartRateOmeter.Bpm) {
        // Log.v("HeartRateOmeter", "[onBpm] $bpm")
        label.setText(bpm.value.toString())
    }

    private fun onFingerChange(fingerDetected: Boolean){

        finger.text = "$fingerDetected"
        if (fingerDetected==true)
        {
            cdt!!.start()
            tv_put_finger.visibility=View.GONE

        }
        else
        {
            cdt!!.cancel()
            tv_put_finger.visibility=View.VISIBLE
        }
    }
// region lifecycle


    override fun onResume() {
        super.onResume()

        cdt=object : CountDownTimer(25000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tv_counter.setText(resources.getString(R.string.time_remain) + millisUntilFinished / 1000 + resources.getString(R.string.sec));
            }

            override fun onFinish() {
                val intent = Intent(this@PulseCounter, Count::class.java)
                val b = Bundle()
                if(label.text.toString()=="")
                {
                    label.setText(R.string.zero)
                }
                b.putInt("count", label.text.toString().toInt())
                intent.putExtras(b)
                var userpulse= UserPulse(label.text.toString())
                Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbeat").setValue(userpulse)
                Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userpulse)
                startActivity(intent)
                finish()
            }
        }
        dispose()
        subscription = CompositeDisposable()
        startWithPermissionCheck()
    }

    override fun onPause() {
        dispose()
        super.onPause()
    }

    private fun dispose() {
        if (subscription?.isDisposed == false)
            subscription?.dispose()
    }

// endregion

// region permission

    companion object {
        private val REQUEST_CAMERA_PERMISSION = 123
    }

    private fun checkPermissions(callbackId: Int, vararg permissionsId: String) {
        when {
            !hasPermission(*permissionsId) -> try {
                ActivityCompat.requestPermissions(this, permissionsId, callbackId)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun hasPermission(vararg permissionsId: String): Boolean {
        var hasPermission = true

        permissionsId.forEach { permission ->
            hasPermission = hasPermission
                    && ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

        return hasPermission
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startWithPermissionCheck()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        cdt!!.cancel()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.getItemId() === android.R.id.home) {
            cdt!!.cancel()
            finish() // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item)
    }
// endregion
}