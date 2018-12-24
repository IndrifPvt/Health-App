package com.softuvo.healthapp.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.softuvo.healthapp.R
import kotlinx.android.synthetic.main.activity_count.*

class Count : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count)

        val b = intent.extras
        val cal = b!!.getInt("count")
        tv_pulse.setText(cal.toString())
    }
}
