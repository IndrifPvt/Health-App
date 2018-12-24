package com.softuvo.healthapp.ui.activities

import android.app.ActionBar
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.softuvo.healthapp.R
import com.softuvo.healthapp.core.BaseActivity
import kotlinx.android.synthetic.main.activity_bmicalculator.*
import kotlinx.android.synthetic.main.activity_bmr_calculator.*
import kotlinx.android.synthetic.main.activity_calories_counter.*
import kotlinx.android.synthetic.main.activity_excersise_burn.*
import android.content.Intent
import android.graphics.Typeface
import android.view.Gravity
import android.view.MenuItem
import android.view.WindowManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.softuvo.healthapp.model.UserBmr
import com.softuvo.healthapp.model.UserCalorie
import com.softuvo.utils.CommonUtils


class CaloriesCounter : BaseActivity() {
    lateinit var Db: DatabaseReference
    lateinit var Auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    override fun onClick(v: View) {

        when (v.id) {
            R.id.btn_calculate_cal -> {

               calculateCalorie()
            }
            R.id.checklist -> {

                val intent = Intent(this@CaloriesCounter, Calories::class.java)
                startActivity(intent)
            }
        }
    }


    private val paths = arrayOf("kg","pounds")
    private val height = arrayOf("metre","inch")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories_counter)
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

        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Db = FirebaseDatabase.getInstance().getReference("Calories")
        Auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
       /* val imm = getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)*/
        val adapter = ArrayAdapter(
            this@CaloriesCounter,
            android.R.layout.simple_spinner_item, paths
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_cal_weight.setAdapter(adapter);
        val adap = ArrayAdapter(
            this@CaloriesCounter,
            android.R.layout.simple_spinner_item, height
        )
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_height_cal.setAdapter(adap);
        et_age_calorie.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var fheight = et_age_calorie.text.toString()
                if (fheight == "") {
                    et_age_calorie.setText("0")
                }
            } else {
                et_age_calorie.text.clear()
            }
        })
        et_height_feets_cal.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var iheight = et_height_feets_cal.text.toString()
                if (iheight == "") {
                    et_height_feets_cal.setText("0")
                }
            } else {
                et_height_feets_cal.text.clear()
            }
        })
        et_height_inchs_cal.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var fheight = et_height_inchs_cal.text.toString()
                if (fheight == "") {
                    et_height_inchs_cal.setText("0")
                }
            } else {
                et_height_inchs_cal.text.clear()
            }
        })
        et_height_met_cal.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var iheight = et_height_met_cal.text.toString()
                if (iheight == "") {
                    et_height_met_cal.setText("0")
                }
            } else {
                et_height_met_cal.text.clear()
            }
        })
        et_height_cm_cal.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var iheight = et_height_cm_cal.text.toString()
                if (iheight == "") {
                    et_height_cm_cal.setText("0")
                }
            } else {
                et_height_cm_cal.text.clear()
            }
        })
        et_weight_cal.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var iheight = et_weight_cal.text.toString()
                if (iheight == "") {
                    et_weight_cal.setText("0")
                }
            } else {
                et_weight_cal.text.clear()
            }
        })

        et_weight_cal.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                var immm = getSystemService(
                    Activity.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                immm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                var nweight = et_weight_cal.text.toString()
                if (nweight == "") {
                    et_weight_cal.setText("0")
                } else {
                    et_weight_cal.setText(nweight)
                }
            }
            false
        })
        spinner_height_cal?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (spinner_height_cal.selectedItem.toString()=="inch")
                {
                    tv_height_met_cal.visibility= View.GONE
                    et_height_met_cal.visibility= View.GONE
                    tv_met_cal.visibility= View.GONE
                    et_height_cm_cal.visibility= View.GONE
                    tv_cm_cal.visibility= View.GONE
                    tv_cal_height.visibility= View.VISIBLE
                    et_height_feets_cal.visibility= View.VISIBLE
                    tv_feets_cal.visibility= View.VISIBLE
                    et_height_inchs_cal.visibility= View.VISIBLE
                    tv_inchs_cal.visibility= View.VISIBLE
                }
                else
                {
                    tv_cal_height.visibility= View.GONE
                    et_height_feets_cal.visibility= View.GONE
                    tv_feets_cal.visibility= View.GONE
                    et_height_inchs_cal.visibility= View.GONE
                    tv_inchs_cal.visibility= View.GONE
                    tv_height_met_cal.visibility= View.VISIBLE
                    et_height_met_cal.visibility= View.VISIBLE
                    tv_met_cal.visibility= View.VISIBLE
                    et_height_cm_cal.visibility= View.VISIBLE
                    tv_cm_cal.visibility= View.VISIBLE
                }
            }
        }
    }

    private fun calculateCalorie() {

        if(et_age_calorie.text.toString()=="")
        {
            et_age_calorie.setText(R.string.zero)
        }
        if(et_height_feets_cal.visibility==View.VISIBLE){
            if(et_height_feets_cal.text.toString()=="")
            et_height_feets_cal.setText(R.string.zero) }
        if(et_height_inchs_cal.visibility==View.VISIBLE){
            if(et_height_inchs_cal.text.toString()=="")
            et_height_inchs_cal.setText(R.string.zero) }
        if(et_height_met_cal.visibility==View.VISIBLE) {
            if(et_height_met_cal.text.toString()=="")
            et_height_met_cal.setText(R.string.zero)
        }
        if(et_height_cm_cal.visibility==View.VISIBLE){
            if(et_height_cm_cal.text.toString()=="")
            et_height_cm_cal.setText(R.string.zero)}
        if(et_weight_cal.text.toString()=="")
        {
            et_weight_cal.setText(R.string.zero)
        }
        val selectedId = radioGroup_calorie.getCheckedRadioButtonId()
        var age = et_age_calorie.text.toString().trim().toInt()
        var height_feet = et_height_feets_cal.text.toString().trim().toInt()
        var height_inch = et_height_inchs_cal.text.toString().trim().toInt()
        var height_met = et_height_met_cal.text.toString().trim().toInt()
        var height_cm = et_height_cm_cal.text.toString().trim().toInt()
        var weight = et_weight_cal.text.toString().trim().toInt()
        /* if (TextUtils.isEmpty(et_age.toString().trim())) {
             Toast.makeText(applicationContext, "Enter age!", Toast.LENGTH_SHORT).show()
             return
         }
        else
         if (TextUtils.isEmpty(et_height_feet.toString().trim())&& TextUtils.isEmpty(et_height_inch.toString().trim())) {
             Toast.makeText(applicationContext, "Enter Height!", Toast.LENGTH_SHORT).show()
             return
         }else
         if (TextUtils.isEmpty(et_weight.toString().trim())) {
             Toast.makeText(applicationContext, "Enter Weight!", Toast.LENGTH_SHORT).show()
             return
         }*/

        if(age==0 )
        {
            Toast.makeText(applicationContext, R.string.correct_age, Toast.LENGTH_SHORT).show()
            return
        }
        if(et_height_met_cal.visibility==View.VISIBLE) {
            if (height_met == 0) {
                Toast.makeText(applicationContext, R.string.correct_height, Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(et_height_feets_cal.visibility==View.VISIBLE) {
            if (height_feet == 0) {
                Toast.makeText(applicationContext, R.string.correct_height, Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(weight==0)
        {
            Toast.makeText(applicationContext, R.string.correct_weight, Toast.LENGTH_SHORT).show()
            return
        }
        if(selectedId==R.id.rb_male_calorie)
        {
            if(spinner_height_cal.selectedItem.toString()=="inch") {
                try {
                    if (spinner_cal_weight.getSelectedItem().toString() == "kg") {
                        var calories =
                            66 + (6.3 * weight*2.20) + (12.9 * (height_feet*12+height_inch)) - (6.8 * age);
                        val intent = Intent(this@CaloriesCounter, EverydayCalorieCount::class.java)
                        var usercal= UserCalorie(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).child("Required Calorie").setValue(usercal)
                        val b = Bundle()
                        b.putDouble("calorie", calories)
                        intent.putExtras(b)
                        startActivity(intent)
                    } else {
                        var calories =
                            66 + (6.3 * (weight)) + (12.9 * (height_feet*12+height_inch)) - (6.8 * age);
                        val intent = Intent(this@CaloriesCounter, EverydayCalorieCount::class.java)
                        var usercal= UserCalorie(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).child("Required Calorie").setValue(usercal)
                        val b = Bundle()
                        b.putDouble("calorie", calories)
                        intent.putExtras(b)
                        startActivity(intent)
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    //somehow workout the issue with an improper input. It's up to your business logic.
                }
            }
            else
            {
                try {
                    if (spinner_cal_weight.getSelectedItem().toString() == "kg") {
                        var calories =
                            66 + (6.3 * weight*2.20) + (12.9 * ((height_met*100+height_cm)/2.54)) - (6.8 * age);
                        val intent = Intent(this@CaloriesCounter, EverydayCalorieCount::class.java)
                        var usercal= UserCalorie(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).child("Required Calorie").setValue(usercal)
                        val b = Bundle()
                        b.putDouble("calorie", calories)
                        intent.putExtras(b)
                        startActivity(intent)
                    } else {
                        var calories =
                            66 + (6.3 * (weight)) + (12.9 * ((height_met*100+height_cm)/2.54)) - (6.8 * age);
                        val intent = Intent(this@CaloriesCounter, EverydayCalorieCount::class.java)
                        var usercal= UserCalorie(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).child("Required Calorie").setValue(usercal)
                        val b = Bundle()
                        b.putDouble("calorie", calories)
                        intent.putExtras(b)
                        startActivity(intent)
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    //somehow workout the issue with an improper input. It's up to your business logic.
                }
            }
        }
        else if(selectedId==R.id.rb_female_calorie)
        {
            if(spinner_height_cal.selectedItem.toString()=="inch") {
                try {
                    if (spinner_cal_weight.getSelectedItem().toString() == "kg") {
                        var calories =
                            655 + (4.3*weight*2.20) + (4.7*(height_feet*12+height_inch)) - (4.7 * age);
                        val intent = Intent(this@CaloriesCounter, EverydayCalorieCount::class.java)
                        var usercal= UserCalorie(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).child("Required Calorie").setValue(usercal)
                        val b = Bundle()
                        b.putDouble("calorie", calories)
                        intent.putExtras(b)
                        startActivity(intent)
                    } else {
                        var calories =
                            655 + (4.3*(weight)) + (4.7*(height_feet*12+height_inch)) - (4.7 * age);
                        val intent = Intent(this@CaloriesCounter, EverydayCalorieCount::class.java)
                        var usercal= UserCalorie(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).child("Required Calorie").setValue(usercal)
                        val b = Bundle()
                        b.putDouble("calorie", calories)
                        intent.putExtras(b)
                        startActivity(intent)
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    //somehow workout the issue with an improper input. It's up to your business logic.
                }
            }
            else
            {
                try {
                    if (spinner_cal_weight.getSelectedItem().toString() == "kg") {
                        var calories =
                            655 + (4.3 * weight*2.20) + (4.7 * ((height_met*100+height_cm)/2.54)) - (4.7 * age);
                        val intent = Intent(this@CaloriesCounter, EverydayCalorieCount::class.java)
                        var usercal= UserCalorie(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).child("Required Calorie").setValue(usercal)
                        val b = Bundle()
                        b.putDouble("calorie", calories)
                        intent.putExtras(b)
                        startActivity(intent)
                    } else {
                        var calories =
                            655 + (4.3 *(weight)) + (4.7 * ((height_met*100+height_cm)/2.54)) - (4.7 * age);
                        val intent = Intent(this@CaloriesCounter, EverydayCalorieCount::class.java)
                        var usercal= UserCalorie(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).child("Required Calorie").setValue(usercal)
                        val b = Bundle()
                        b.putDouble("calorie", calories)
                        intent.putExtras(b)
                        startActivity(intent)
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()

                }
            }
        }
        else
        {
            Toast.makeText(applicationContext, R.string.choose_gender, Toast.LENGTH_SHORT).show()
            return
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
