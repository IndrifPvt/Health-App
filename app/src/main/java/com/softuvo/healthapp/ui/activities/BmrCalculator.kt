package com.softuvo.healthapp.ui.activities

import android.app.ActionBar
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.softuvo.healthapp.R
import com.softuvo.healthapp.core.BaseActivity
import com.softuvo.utils.Validations
import kotlinx.android.synthetic.main.activity_bmr_calculator.*
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.support.v4.content.ContextCompat.getSystemService
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.*
import android.view.View.VISIBLE
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.softuvo.healthapp.model.UserBmr
import com.softuvo.healthapp.model.UserData
import com.softuvo.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_bmicalculator.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import android.view.Gravity
import android.view.ViewGroup




public class BmrCalculator : BaseActivity() {
    private lateinit var imm:InputMethodManager
    private val paths = arrayOf("kg","pounds")
    private val height = arrayOf("metre","inch")
    lateinit var Db: DatabaseReference
    lateinit var Auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_calculate_bmr -> {
               calculate()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmr_calculator)
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

        Db = FirebaseDatabase.getInstance().getReference("BMR")
        Auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /*val imm = getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)*/
        tv_calories.visibility=View.GONE

        val adapter = ArrayAdapter(
            this@BmrCalculator,
            android.R.layout.simple_spinner_item, paths
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_weight.setAdapter(adapter);
        val adap = ArrayAdapter(
            this@BmrCalculator,
            android.R.layout.simple_spinner_item, height
        )
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_height_bmr.setAdapter(adap);
        et_age.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var nage=et_age.text.toString()
                if(nage=="")
                {
                    et_age.setText("0")
                }
            }
            else {
                et_age.text.clear()
            }
        })
        et_height_feet.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var fheight=et_height_feet.text.toString()
                if(fheight=="")
                {
                    et_height_feet.setText("0")
                }
            }
            else
            {
                et_height_feet.text.clear()
            }
        })
        et_height_inch.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                var iheight=et_height_inch.text.toString()
                if(iheight=="")
                {
                    et_height_inch.setText("0")
                }
            }
            else
            {
                et_height_inch.text.clear()
            }
        })
        et_height_met_bmr.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var fheight=et_height_met_bmr.text.toString()
                if(fheight=="")
                {
                    et_height_met_bmr.setText("0")
                }
            }
            else
            {
                et_height_met_bmr.text.clear()
            }
        })
        et_height_cm_bmr.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                var iheight=et_height_cm_bmr.text.toString()
                if(iheight=="")
                {
                    et_height_cm_bmr.setText("0")
                }
            }
            else
            {
                et_height_cm_bmr.text.clear()
            }
        })
        et_weight.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var nweight=et_weight.text.toString()
                if(nweight=="")
                {
                    et_weight.setText("0")
                }
            }
            else
            {
                et_weight.text.clear()
            }
        })
        et_weight.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                var immm = getSystemService(
                    Activity.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                immm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                var nweight=et_weight.text.toString()
                if (nweight=="")
                {
                    et_weight.setText("0")
                }
                else
                {
                    et_weight.setText(nweight)
                }
            }
            false
        })
        spinner_height_bmr?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (spinner_height_bmr.selectedItem.toString()=="inch")
                {
                    tv_height_bmr.visibility=View.GONE
                    et_height_met_bmr.visibility=View.GONE
                    tv_met_bmr.visibility=View.GONE
                    et_height_cm_bmr.visibility=View.GONE
                    tv_bmr_cm.visibility=View.GONE
                    tv_height.visibility=View.VISIBLE
                    et_height_feet.visibility=View.VISIBLE
                    tv_feet.visibility=View.VISIBLE
                    et_height_inch.visibility=View.VISIBLE
                    tv_inch.visibility=View.VISIBLE
                }
                else
                {
                    tv_height.visibility=View.GONE
                    et_height_feet.visibility=View.GONE
                    tv_feet.visibility=View.GONE
                    et_height_inch.visibility=View.GONE
                    tv_inch.visibility=View.GONE
                    tv_height_bmr.visibility=View.VISIBLE
                    et_height_met_bmr.visibility=View.VISIBLE
                    tv_met_bmr.visibility=View.VISIBLE
                    et_height_cm_bmr.visibility=View.VISIBLE
                    tv_bmr_cm.visibility=View.VISIBLE
                }
            }
        }
    }

    private fun calculate() {

        if(et_age.text.toString()=="")
        {
            et_age.setText(R.string.zero)
        }
        if (et_height_feet.visibility== VISIBLE) {
            if (et_height_feet.text.toString() == "") {
                et_height_feet.setText(R.string.zero)
            }
        }
        if (et_height_met_bmr.visibility== VISIBLE) {
            if (et_height_met_bmr.text.toString() == "") {
                et_height_met_bmr.setText(R.string.zero)
            }
        }
        if (et_height_inch.visibility== VISIBLE) {
            if (et_height_inch.text.toString() == "") {
                et_height_inch.setText(R.string.zero)
            }
        }
        if (et_height_cm_bmr.visibility== VISIBLE) {
            if (et_height_cm_bmr.text.toString() == "") {
                et_height_cm_bmr.setText(R.string.zero)
            }
        }
        if(et_weight.text.toString()=="")
        {
            et_weight.setText(R.string.zero)
        }
        val selectedId = radioGroup.getCheckedRadioButtonId()
        var age = et_age.text.toString().trim().toInt()
        var height_feet = et_height_feet.text.toString().trim().toInt()
        var height_inch = et_height_inch.text.toString().trim().toInt()
        var height_met = et_height_met_bmr.text.toString().trim().toInt()
        var height_cm = et_height_cm_bmr.text.toString().trim().toInt()
        var weight = et_weight.text.toString().trim().toInt()
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
        if(et_height_met_bmr.visibility==View.VISIBLE) {
            if (height_met == 0) {
                Toast.makeText(applicationContext, R.string.correct_height, Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(et_height_feet.visibility==View.VISIBLE) {
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
        if(selectedId==R.id.rb_male)
        {
            try {
                if (spinner_height_bmr.selectedItem.toString()=="metre") {
                    if (spinner_weight.getSelectedItem().toString() == "kg") {
                        var calories =
                            10 * weight!! + 6.25 * (height_met!! * 100.0 + height_cm) - 5 * age!! + 5
                        tv_calories.visibility = View.VISIBLE
                        tv_calories.setText(calories.toString())
                        var userbmr= UserBmr(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmr").setValue(userbmr)
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmr)

                    } else {
                        var calories =
                            10 * (weight!! / 2.20) + 6.25 * (height_met!! * 100.0 + height_cm) - 5 * age!! + 5
                        tv_calories.visibility = View.VISIBLE
                        tv_calories.setText(calories.toString())
                        var userbmr= UserBmr(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmr").setValue(userbmr)
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmr)

                    }
                }
                else
                {
                    if (spinner_weight.getSelectedItem().toString() == "kg") {
                        var calories =
                            10 * weight!! + 6.25 * (height_feet!! * 30.48 + height_inch!! * 2.54) - 5 * age!! + 5
                        tv_calories.visibility = View.VISIBLE
                        tv_calories.setText(calories.toString())
                        var userbmr= UserBmr(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmr").setValue(userbmr)
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmr)

                    } else {
                        var calories =
                            10 * (weight!! / 2.20) + 6.25 * (height_feet!! * 30.48 + height_inch!! * 2.54) - 5 * age!! + 5
                        tv_calories.visibility = View.VISIBLE
                        tv_calories.setText(calories.toString())
                        var userbmr= UserBmr(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmr").setValue(userbmr)
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmr)

                    }
                }
            }

            catch (e: NumberFormatException) {
                e.printStackTrace()
                //somehow workout the issue with an improper input. It's up to your business logic.
            }

        }
        else if(selectedId==R.id.rb_female){
            try {
                if (spinner_height_bmr.selectedItem.toString()=="metre") {
                    if (spinner_weight.getSelectedItem().toString() == "kg") {
                        var calories =
                            10 * weight!! + 6.25 * (height_met!! * 100.0 + height_cm) - 5 * age!! - 161
                        tv_calories.visibility = View.VISIBLE
                        tv_calories.setText(calories.toString())
                        var userbmr= UserBmr(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmr").setValue(userbmr)
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmr)

                    } else {
                        var calories =
                            10 * (weight!! / 2.20) + 6.25 * (height_met!! * 100.0 + height_cm) - 5 * age!! - 161
                        tv_calories.visibility = View.VISIBLE
                        tv_calories.setText(calories.toString())
                        var userbmr= UserBmr(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmr").setValue(userbmr)
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmr)

                    }
                }
                else
                {
                    if (spinner_weight.getSelectedItem().toString() == "kg") {
                        var calories =
                            10 * weight!! + 6.25 * (height_feet!! * 30.48 + height_inch!! * 2.54) - 5 * age!! - 161
                        tv_calories.visibility = View.VISIBLE
                        tv_calories.setText(calories.toString())
                        var userbmr= UserBmr(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmr").setValue(userbmr)
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmr)

                    } else {
                        var calories =
                            10 * (weight!! / 2.20) + 6.25 * (height_feet!! * 30.48 + height_inch!! * 2.54) - 5 * age!! - 161
                        tv_calories.visibility = View.VISIBLE
                        tv_calories.setText(calories.toString())
                        var userbmr= UserBmr(String.format("%.3f", calories))
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmr").setValue(userbmr)
                        Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmr)

                    }
                }


            } catch (e: NumberFormatException) {
                e.printStackTrace()
                //somehow workout the issue with an improper input. It's up to your business logic.
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
