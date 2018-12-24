package com.softuvo.healthapp.ui.activities

import android.app.ActionBar
import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.softuvo.healthapp.R
import com.softuvo.healthapp.core.BaseActivity
import com.softuvo.healthapp.model.User
import com.softuvo.healthapp.model.UserData
import com.softuvo.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_bmicalculator.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import android.support.constraint.ConstraintLayout
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.support.constraint.ConstraintSet




class BMICalculator : BaseActivity() {
    lateinit var Db: DatabaseReference
    lateinit var Auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private lateinit var user: FirebaseUser
    private  var userdata: UserData = UserData()
    private  var users: User = User()
    private val paths = arrayOf("kg","pounds")
    private val height = arrayOf("metre","inch")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_calculate_bmi -> {
                calculatebmi()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmicalculator)
        showProgressDialog()
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        var textView = TextView(this);
        textView.setText(title);
        textView.setTextSize(20F);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams( LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.colorWhite));
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM or ActionBar.DISPLAY_HOME_AS_UP
        getSupportActionBar()!!.setCustomView(textView);

        Db = FirebaseDatabase.getInstance().getReference("BMI")
        Auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        var bmi = Db.child(Auth.currentUser!!.email!!.replace(".",",")).child("lastbmi")
        bmi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val nuser = dataSnapshot.getValue(UserData::class.java)
                if (nuser!=null) {
                    tv_bmi_previous.setText(nuser!!.bmi)
                    hideProgressDialog()
                }
                else
                {
                    hideProgressDialog()
                    Toast.makeText(context,resources.getString(R.string.no_data),Toast.LENGTH_SHORT).show()
                    return
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println(resources.getString(R.string.the_read) + databaseError.code)
            }
        })

        /*val imm = getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)*/
        val adapter = ArrayAdapter(
            this@BMICalculator,
            android.R.layout.simple_spinner_item, paths
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_bmi_weight.setAdapter(adapter);
        val adap = ArrayAdapter(
            this@BMICalculator,
            android.R.layout.simple_spinner_item, height
        )
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_height.setAdapter(adap);
        spinner_height?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (spinner_height.selectedItem.toString()=="inch")
                {
                    tv_height_met.visibility=View.GONE
                    et_height_met.visibility=View.GONE
                    tv_met.visibility=View.GONE
                    et_height_cm.visibility=View.GONE
                    tv_cm.visibility=View.GONE
                    tv_bmi_height.visibility=View.VISIBLE
                    et_height_feets.visibility=View.VISIBLE
                    tv_feets.visibility=View.VISIBLE
                    et_height_inchs.visibility=View.VISIBLE
                    tv_inchs.visibility=View.VISIBLE
                }
                else
                {
                    tv_bmi_height.visibility=View.GONE
                    et_height_feets.visibility=View.GONE
                     tv_feets.visibility=View.GONE
                    et_height_inchs.visibility=View.GONE
                     tv_inchs.visibility=View.GONE
                    tv_height_met.visibility=View.VISIBLE
                    et_height_met.visibility=View.VISIBLE
                     tv_met.visibility=View.VISIBLE
                    et_height_cm.visibility=View.VISIBLE
                    tv_cm.visibility=View.VISIBLE
                }
            }
        }
        et_height_feets.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var fheight = et_height_feets.text.toString()
                if (fheight == "") {
                    et_height_feets.setText("0")
                }
            } else {
                et_height_feets.text.clear()
            }
        })
        et_height_inchs.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var iheight = et_height_inchs.text.toString()
                if (iheight == "") {
                    et_height_inchs.setText("0")
                }
            } else {
                et_height_inchs.text.clear()
            }
        })
        et_height_met.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var fheight = et_height_met.text.toString()
                if (fheight == "") {
                    et_height_met.setText("0")
                }
            } else {
                et_height_met.text.clear()
            }
        })
        et_height_cm.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var iheight = et_height_cm.text.toString()
                if (iheight == "") {
                    et_height_cm.setText("0")
                }
            } else {
                et_height_cm.text.clear()
            }
        })
        et_weight_bmi.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var nweight = et_weight_bmi.text.toString()
                if (nweight == "") {
                    et_weight_bmi.setText("0")
                }
            } else {
                et_weight_bmi.text.clear()
            }
        })
        et_weight_bmi.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                var immm = getSystemService(
                    Activity.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                immm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                var nweight = et_weight_bmi.text.toString()
                if (nweight == "") {
                    et_weight_bmi.setText("0")
                } else {
                    et_weight_bmi.setText(nweight)
                }

            }
            false
        })

    }

    private fun calculatebmi() {

        if(et_height_feets.text.toString()=="" || et_height_inchs.text.toString()=="" || et_height_met.text.toString()=="" ||et_height_cm.text.toString()=="" || et_weight_bmi.text.toString()==""  )
        {
            if(et_height_feets.visibility==View.VISIBLE){
            et_height_feets.setText(R.string.zero) }
            if(et_height_inchs.visibility==View.VISIBLE){
            et_height_inchs.setText(R.string.zero) }
            if(et_height_met.visibility==View.VISIBLE) {
                et_height_met.setText(R.string.zero)
            }
            if(et_height_cm.visibility==View.VISIBLE){
            et_height_cm.setText(R.string.zero)}

            et_weight_bmi.setText(R.string.zero)
        }

        var height_feet = et_height_feets.text.toString().trim().toInt()
        var height_inch = et_height_inchs.text.toString().trim().toInt()
        var height_met = et_height_met.text.toString().trim().toInt()
        var height_cm = et_height_cm.text.toString().trim().toInt()
        var weight = et_weight_bmi.text.toString().trim().toInt()
        if(et_height_met.visibility==View.VISIBLE) {
            if (height_met == 0) {
                Toast.makeText(applicationContext, R.string.correct_height, Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(et_height_feets.visibility==View.VISIBLE) {
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

        if(spinner_height.selectedItem.toString()=="metre")
        {
            try {
                if(spinner_bmi_weight.getSelectedItem().toString()=="kg") {
                   var bmi= weight/((height_met+(height_cm/100.0))*(height_met+(height_cm/100.0)))
                    tv_bmi.setText(String.format("%.3f", bmi))
                    var userbmi=UserData(String.format("%.3f", bmi))
                    Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmi").setValue(userbmi)
                    Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmi)

                }
                else{
                    var bmi= (weight/2.20)/((height_met+(height_cm/100.0))*(height_met+(height_cm/100.0)))
                    tv_bmi.setText(String.format("%.3f", bmi))
                    var userbmi=UserData(String.format("%.3f", bmi))
                    Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmi").setValue(userbmi)
                    Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmi)

                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                //somehow workout the issue with an improper input. It's up to your business logic.
            }

        }
        else {
            try {
                if(spinner_bmi_weight.getSelectedItem().toString()=="kg") {
                    var bmi= ((weight*2.20)/(((height_feet*12.0)+height_inch)*((height_feet*12)+height_inch)))*703
                    tv_bmi.setText(String.format("%.3f", bmi))
                    var userbmi=UserData(String.format("%.3f", bmi))
                    Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmi").setValue(userbmi)
                    Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmi)


                }
                else{
                    var hght= (height_feet*12.0)+height_inch
                    var bmi= (weight/(hght*hght))*703
                    tv_bmi.setText(String.format("%.3f", bmi))
                    var userbmi=UserData(String.format("%.3f", bmi))
                    Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastbmi").setValue(userbmi)
                    Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(userbmi)


                }

            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
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
