package com.softuvo.healthapp.ui.activities

import android.app.ActionBar
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.softuvo.healthapp.R
import com.softuvo.healthapp.core.BaseActivity
import com.softuvo.healthapp.data.interfaces.StepListener
import com.softuvo.healthapp.model.UserSteps
import com.softuvo.healthapp.ui.steps.StepDetector
import com.softuvo.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_step_counter.*
import android.content.Intent
import android.graphics.Typeface
import android.view.Gravity
import android.view.MenuItem
import android.widget.*


class StepCounter : BaseActivity(), SensorEventListener, StepListener {

    override fun onClick(v: View) {

    }
    lateinit var Db: DatabaseReference
    lateinit var Auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private var TvSteps: TextView? = null
    private var simpleStepDetector: StepDetector? = null
    private var sensorManager: SensorManager? = null
    private var accel: Sensor? = null
    private var BtnStart: Button? = null
    private var BtnStop:Button? = null
    private val TEXT_NUM_STEPS = "Number of Steps: "
    private var numSteps: Int = 0
    private  var totalstep:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)
        var mIntent = intent
         totalstep = mIntent.getIntExtra("Steps", 0)
        btn_stop.visibility=View.GONE
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
        Db = FirebaseDatabase.getInstance().getReference("Step Count")
        Auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accel = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        simpleStepDetector = StepDetector()
        simpleStepDetector!!.registerListener(this)

        TvSteps = findViewById(R.id.tv_steps_count) as TextView
        BtnStart = findViewById(R.id.btn_start) as Button
        BtnStop = findViewById(R.id.btn_stop) as Button


        btn_start.setOnClickListener(View.OnClickListener {
            btn_stop.visibility=View.VISIBLE
            numSteps = 0
            sensorManager!!.registerListener(this@StepCounter, accel, SensorManager.SENSOR_DELAY_FASTEST)

        })

        btn_stop.setOnClickListener(View.OnClickListener {

            sensorManager!!.unregisterListener(this@StepCounter)
            if(tv_steps_count.text.toString()=="")
            {
                tv_steps_count.setText("0")
            }
            var todaystep=tv_steps_count.text.toString().toInt()
            var totalsteps=totalstep+todaystep
            var step=UserSteps(totalsteps.toString())
            Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(step)
            finish()

        })

    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector!!.updateAccel(
                event.timestamp, event.values[0], event.values[1], event.values[2]
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun step(timeNs: Long) {
        numSteps++
        TvSteps!!.setText((numSteps * 2).toString())
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.getItemId() === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item)
    }
}
