package com.softuvo.healthapp.ui.activities

import android.app.ActionBar
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.softuvo.healthapp.R
import com.softuvo.healthapp.core.BaseActivity
import com.softuvo.healthapp.model.UserCalorie
import com.softuvo.healthapp.model.UserData
import com.softuvo.healthapp.model.UserEveryDayCalorie
import com.softuvo.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_bmicalculator.*
import kotlinx.android.synthetic.main.activity_calories.*
import kotlinx.android.synthetic.main.layout_calories.*

class Calories : BaseActivity() {

    lateinit var Db: DatabaseReference
    lateinit var Auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    override fun onClick(v: View) {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories)
        showProgressDialog()
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
        Db = FirebaseDatabase.getInstance().getReference("Calories")
        Auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        var getcal = Db.child(Auth.currentUser!!.email!!.replace(".",",")).child(CommonUtils.getCurrentDateTime()).child("Calorie Get")
        getcal.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val nuser = dataSnapshot.getValue(UserEveryDayCalorie::class.java)
                if (nuser!=null) {
                   et_cal_breakfast.setText(nuser.breakfast)
                    et_cal_lunch.setText(nuser.lunch)
                    et_cal_dinner.setText(nuser.dinner)
                    et_cal_snacks.setText(nuser.snacks)
                    et_cal_other.setText(nuser.other)
                    et_cal_total.setText(nuser.totalcalories)
                    hideProgressDialog()
                }
                else
                {
                    hideProgressDialog()
                    Toast.makeText(context,R.string.no_data, Toast.LENGTH_SHORT).show()
                    return

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println(R.string.the_read + databaseError.code)
            }
        })
        var reqcal = Db.child(Auth.currentUser!!.email!!.replace(".",",")).child(CommonUtils.getCurrentDateTime()).child("Required Calorie")
        reqcal.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val nuser = dataSnapshot.getValue(UserCalorie::class.java)
                if (nuser!=null) {
                    tv_cal_required_calorie_value.setText(nuser.requiredcalorie)
                    hideProgressDialog()
                }
                else
                {
                    hideProgressDialog()
                    Toast.makeText(context,R.string.no_data, Toast.LENGTH_SHORT).show()
                    return

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println(R.string.no_data + databaseError.code)
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
