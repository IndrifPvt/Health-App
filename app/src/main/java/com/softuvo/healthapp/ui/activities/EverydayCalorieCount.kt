package com.softuvo.healthapp.ui.activities

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.softuvo.healthapp.R
import com.softuvo.healthapp.core.BaseActivity
import com.softuvo.healthapp.model.UserEveryDayCalorie
import com.softuvo.healthapp.model.UserSteps
import com.softuvo.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_bmr_calculator.*
import kotlinx.android.synthetic.main.activity_everyday_calorie_count.*
import kotlinx.android.synthetic.main.content_dashboard.*

class EverydayCalorieCount : BaseActivity() {
    lateinit var dbcalorie: DatabaseReference
    lateinit var Auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference

    override fun onClick(v: View) {
           caloriecount()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_everyday_calorie_count)
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
        Auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        dbcalorie = FirebaseDatabase.getInstance().getReference("Calories")
        val b = intent.extras
        val cal = b!!.getDouble("calorie")
        tv_required_calorie_value.setText(String.format("%.3f", cal))
        var calorie = dbcalorie.child(Auth.currentUser!!.email!!.replace(".",",")).child(CommonUtils.getCurrentDateTime()).child("Calorie Get")
        calorie.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var cal = dataSnapshot.getValue(UserEveryDayCalorie::class.java)
                if(cal!=null)
                {
                    et_breakfast.setText(cal!!.breakfast)
                    et_lunch.setText(cal!!.lunch)
                    et_dinner.setText(cal!!.dinner)
                    et_snacks.setText(cal!!.snacks)
                    et_other.setText(cal!!.other)
                    hideProgressDialog()
                }
                else
                {
                    hideProgressDialog()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println(R.string.the_read + databaseError.code)
            }
        })
        et_breakfast.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var nage = et_breakfast.text.toString()
                if (nage == "") {
                    et_breakfast.setText("0")
                }
            } else {
                et_breakfast.text.clear()
            }
        })
        et_lunch.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var fheight = et_lunch.text.toString()
                if (fheight == "") {
                    et_lunch.setText("0")
                }
            } else {
                et_lunch.text.clear()
            }
        })
        et_dinner.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                var iheight = et_dinner.text.toString()
                if (iheight == "") {
                    et_dinner.setText("0")
                }
            } else {
                et_dinner.text.clear()
            }
        })
        et_snacks.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var fheight = et_snacks.text.toString()
                if (fheight == "") {
                    et_snacks.setText("0")
                }
            } else {
                et_snacks.text.clear()
            }
        })

        et_other.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                var iheight = et_other.text.toString()
                if (iheight == "") {
                    et_other.setText("0")

                }
            } else {
                et_other.text.clear()
            }
        })

        et_other.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                var immm = getSystemService(
                    Activity.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                immm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                var nweight = et_other.text.toString()
                if (nweight == "") {
                    et_other.setText("0")
                } else {
                    et_other.setText(nweight)
                }
            }
            false
        })

    }
    private fun caloriecount() {

        if(et_breakfast.text.toString()=="")
        {
            et_breakfast.setText(R.string.zero)
        }
        if(et_lunch.text.toString()=="")
        {
            et_lunch.setText(R.string.zero)
        }
        if(et_dinner.text.toString()=="")
        {
            et_dinner.setText(R.string.zero)
        }
        if(et_snacks.text.toString()=="")
        {
            et_snacks.setText(R.string.zero)
        }
        if(et_other.text.toString()=="")
        {
            et_other.setText(R.string.zero)
        }
        var breakfast=et_breakfast.text.toString().toInt()
        var lunch=et_lunch.text.toString().toInt()
        var dinner=et_dinner.text.toString().toInt()
        var snacks=et_snacks.text.toString().toInt()
        var others=et_other.text.toString().toInt()

        var c=breakfast+lunch+dinner+snacks+others
        var cal=UserEveryDayCalorie(breakfast.toString(),lunch.toString(),dinner.toString(),snacks.toString(),others.toString(),c.toString())
        dbcalorie.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).child("Calorie Get").setValue(cal)
        Toast.makeText(context,resources.getString(R.string.calorie) + (c.toString()),Toast.LENGTH_SHORT).show()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.getItemId() === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item)
    }
}
