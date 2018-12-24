package com.softuvo.healthapp.ui.activities

 import android.app.ActionBar
 import android.app.Activity
 import android.graphics.Typeface
 import android.os.Bundle
 import android.view.Gravity
 import android.view.MenuItem
 import android.view.View
 import android.view.WindowManager
 import android.view.inputmethod.EditorInfo
 import android.view.inputmethod.InputMethodManager
 import android.widget.*
 import com.google.firebase.auth.FirebaseAuth
 import com.google.firebase.database.DatabaseReference
 import com.google.firebase.database.FirebaseDatabase
 import com.google.firebase.storage.FirebaseStorage
 import com.google.firebase.storage.StorageReference
 import com.softuvo.healthapp.R
 import com.softuvo.healthapp.core.BaseActivity
 import com.softuvo.healthapp.model.UserData
 import com.softuvo.healthapp.model.UserExcersise
 import com.softuvo.utils.CommonUtils
 import kotlinx.android.synthetic.main.activity_bmr_calculator.*
 import kotlinx.android.synthetic.main.activity_excersise_burn.*


class ExcersiseBurn : BaseActivity() {
    private  var heights:Double = 0.0
    lateinit var Db: DatabaseReference
    lateinit var Auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
     private val paths = arrayOf("kg", "pounds")
     private val height = arrayOf("metre", "inch")
     override fun onClick(v: View) {
         when (v.id) {
             R.id.btn_calculate_ex -> {
                 excersiseburn()
             }
         }
     }


    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_excersise_burn)
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

       /* getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        val imm = getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)*/
        Db = FirebaseDatabase.getInstance().getReference("Excersise")
        Auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

         val adapter = ArrayAdapter(
             this@ExcersiseBurn,
             android.R.layout.simple_spinner_item, paths
         )
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spinner_excersise_weight.setAdapter(adapter);
         val adap = ArrayAdapter(
             this@ExcersiseBurn,
             android.R.layout.simple_spinner_item, height
         )

         adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spinner_height_ex.setAdapter(adap);
        et_age_ex.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {

                var fheight = et_age_ex.text.toString()
                if (fheight == "") {
                    et_age_ex.setText("0")
                }
            } else {
                et_age_ex.text.clear()
            }
        })
         et_height_feets_ex.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
             if (!hasFocus) {

                 var fheight = et_height_feets_ex.text.toString()
                 if (fheight == "") {
                     et_height_feets_ex.setText("0")
                 }
             } else {
                 et_height_feets_ex.text.clear()
             }
         })

         et_height_inchs_ex.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
             if (!hasFocus) {

                 var iheight = et_height_inchs_ex.text.toString()
                 if (iheight == "") {
                     et_height_inchs_ex.setText("0")
                 }
             } else {
                 et_height_inchs_ex.text.clear()
             }
         })

         et_height_met_ex.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
             if (!hasFocus) {

                 var fheight = et_height_met_ex.text.toString()
                 if (fheight == "") {
                     et_height_met_ex.setText("0")
                 }
             } else {
                 et_height_met_ex.text.clear()
             }
         })
         et_height_cm_ex.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
             if (!hasFocus) {

                 var iheight = et_height_cm_ex.text.toString()
                 if (iheight == "") {
                     et_height_cm_ex.setText("0")
                 }
             } else {
                 et_height_cm_ex.text.clear()
             }
         })
         et_weight_excersise.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
             if (!hasFocus) {

                 var iheight = et_weight_excersise.text.toString()
                 if (iheight == "") {
                     et_weight_excersise.setText("0")
                 }
             } else {
                 et_weight_excersise.text.clear()
             }
         })
         et_steps.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
             if (!hasFocus) {

                 var iheight = et_steps.text.toString()
                 if (iheight == "") {
                     et_steps.setText("0")
                 }
             } else {
                 et_steps.text.clear()
             }
         })
         et_time_hour.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
             if (!hasFocus) {

                 var iheight = et_time_hour.text.toString()
                 if (iheight == "") {
                     et_time_hour.setText("0")
                 }
             } else {
                 et_time_hour.text.clear()
             }
         })

         et_time_min.setOnFocusChangeListener(View.OnFocusChangeListener { view, hasFocus ->
             if (!hasFocus) {

                 var nweight = et_time_min.text.toString()
                 if (nweight == "") {
                     et_time_min.setText("0")
                 }
             } else {
                 et_time_min.text.clear()
             }
         })
         et_time_min.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
             if (actionId == EditorInfo.IME_ACTION_DONE) {

                 var immm = getSystemService(
                     Activity.INPUT_METHOD_SERVICE
                 ) as InputMethodManager
                 immm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                 var nweight = et_time_min.text.toString()
                 if (nweight == "") {
                     et_time_min.setText("0")
                 } else {
                     et_time_min.setText(nweight)
                 }
             }
             false
         })
         spinner_height_ex?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
             override fun onNothingSelected(parent: AdapterView<*>?) {

             }

             override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                 if (spinner_height_ex.selectedItem.toString()=="inch")
                 {
                     tv_height_met_ex.visibility=View.GONE
                     et_height_met_ex.visibility=View.GONE
                     tv_met_ex.visibility=View.GONE
                     et_height_cm_ex.visibility=View.GONE
                     tv_cm_ex.visibility=View.GONE
                     tv_excersise_height.visibility=View.VISIBLE
                     et_height_feets_ex.visibility=View.VISIBLE
                     tv_feets_ex.visibility=View.VISIBLE
                     et_height_inchs_ex.visibility=View.VISIBLE
                     tv_inchs_ex.visibility=View.VISIBLE
                 }
                 else
                 {
                     tv_excersise_height.visibility=View.GONE
                     et_height_feets_ex.visibility=View.GONE
                     tv_feets_ex.visibility=View.GONE
                     et_height_inchs_ex.visibility=View.GONE
                     tv_inchs_ex.visibility=View.GONE
                     tv_height_met_ex.visibility=View.VISIBLE
                     et_height_met_ex.visibility=View.VISIBLE
                     tv_met_ex.visibility=View.VISIBLE
                     et_height_cm_ex.visibility=View.VISIBLE
                     tv_cm_ex.visibility=View.VISIBLE
                 }
             }
         }
     }
    private fun excersiseburn() {

        if(et_age_ex.text.toString()=="")
        {
            et_age_ex.setText(R.string.zero)
        }
        if(et_weight_excersise.text.toString()=="")
        {
            et_weight_excersise.setText(R.string.zero)
        }
        if(et_height_feets_ex.text.toString()=="")
        {
            et_height_feets_ex.setText(R.string.zero)
        }
        if(et_height_inchs_ex.text.toString()=="")
        {
            et_height_inchs_ex.setText(R.string.zero)
        }
        if(et_height_met_ex.text.toString()=="")
        {
            et_height_met_ex.setText(R.string.zero)
        }
        if(et_height_cm_ex.text.toString()=="")
        {
            et_height_cm_ex.setText(R.string.zero)
        }
        if(et_steps.text.toString()=="")
        {
            et_steps.setText(R.string.zero)
        }
        if(et_time_hour.text.toString()=="")
        {
            et_time_hour.setText(R.string.zero)
        }
        if(et_time_min.text.toString()=="")
        {
            et_time_min.setText(R.string.zero)
        }
        val selectedId = radioGroup_ex.getCheckedRadioButtonId()
        var age = et_age_ex.text.toString().trim().toInt()
        var height_feet = et_height_feets_ex.text.toString().trim().toInt()
        var height_inch = et_height_inchs_ex.text.toString().trim().toInt()
        var weight = et_weight_excersise.text.toString().trim().toInt()
        var height_met = et_height_met_ex.text.toString().trim().toInt()
        var height_cm = et_height_cm_ex.text.toString().trim().toInt()
        var steps = et_steps.text.toString().trim().toInt()
        var time_hr = et_time_hour.text.toString().trim().toInt()
        var time_min = et_time_min.text.toString().trim().toInt()
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
         var durationInSeconds=time_hr*60*60+time_min*60
        if(age==0 )
        {
            Toast.makeText(applicationContext, R.string.correct_age, Toast.LENGTH_SHORT).show()
            return
        }
        if(et_height_feets_ex.visibility==View.VISIBLE) {
            if (height_feet == 0) {
                Toast.makeText(applicationContext,  R.string.correct_height, Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(weight==0)
        {
            Toast.makeText(applicationContext,  R.string.correct_weight, Toast.LENGTH_SHORT).show()
            return
        }
        if(et_height_met_ex.visibility==View.VISIBLE) {
            if (height_met == 0) {
                Toast.makeText(applicationContext, R.string.correct_height, Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(steps==0)
        {
            Toast.makeText(applicationContext, R.string.correct_steps, Toast.LENGTH_SHORT).show()
            return
        }
        if(time_min==0)
        {
            Toast.makeText(applicationContext, R.string.correct_time, Toast.LENGTH_SHORT).show()
            return
        }

        if (spinner_height_ex.selectedItem.toString()=="inch")
        {
            if(spinner_excersise_weight.selectedItem.toString()=="kg")
            {
              var res=calculateEnergyExpenditure(((height_feet*12+height_inch)*(2.54/100)),age,
                  weight.toDouble(),selectedId,durationInSeconds,steps,20.0)
                tv_ex_res.setText(String.format("%.3f", res))
                var eb= UserExcersise(String.format("%.3f", res))
                Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastcal").setValue(eb)
                Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(eb)

            }
            else
            {
                var res=calculateEnergyExpenditure(((height_feet*12+height_inch)*(2.54/100)),age,(weight/2.20).toDouble(),selectedId,durationInSeconds,steps,20.0)
                tv_ex_res.setText(String.format("%.3f", res))
                var eb= UserExcersise(String.format("%.3f", res))
                Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastcal").setValue(eb)
                Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(eb)

            }
        }
        else
        {
            if(spinner_excersise_weight.selectedItem.toString()=="kg")
            {
                var res=calculateEnergyExpenditure((height_met*100.0+height_cm),age,weight.toDouble(),selectedId,durationInSeconds,steps,20.0)
                tv_ex_res.setText(String.format("%.3f", res))
                var eb= UserExcersise(String.format("%.3f", res))
                Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastcal").setValue(eb)
                Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(eb)

            }
            else
            {
                var res=calculateEnergyExpenditure((height_met*100.0+(height_cm)),age,(weight/2.20).toDouble(),selectedId,durationInSeconds,steps,20.0)
                tv_ex_res.setText(String.format("%.3f", res))
                var eb= UserExcersise(String.format("%.3f", res))
                Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child("lastcal").setValue(eb)
                Db.child(Auth.currentUser!!.email!!.replace(".", ",")).child(CommonUtils.getCurrentDateTime()).setValue(eb)

            }
        }
    }

    fun calculateEnergyExpenditure(
        height: Double,
        age: Int,
        weight: Double,
        gender: Int,
        durationInSeconds: Int,
        stepsTaken: Int,
        strideLengthInMetres: Double

    ): Double {

        var ageCalculated = age
        var selectedId = radioGroup_ex.getCheckedRadioButtonId()
        if(et_height_met_ex.visibility==View.VISIBLE && et_height_cm_ex.visibility==View.VISIBLE) {
             heights =
                (et_height_met_ex.text.toString().trim().toDouble() *100.0 + (et_height_cm_ex.text.toString().trim().toDouble()) )
        }
        else
        {
             heights =
                ((et_height_feets_ex.text.toString().trim().toDouble() * 12 + et_height_inchs_ex.text.toString().trim().toDouble()) * (2.54))
        }
        val cal = harrisBenedictRmr(
                selectedId,
                weight,
                ageCalculated,
                heights
            )
        var harrisBenedictRmR = convertKilocaloriesToMlKmin(cal,weight)

        var kmTravelled = calculateDistanceTravelledInKM(stepsTaken, strideLengthInMetres)
        var hours = (et_time_hour.text.toString().trim().toDouble() + (et_time_min.text.toString().trim().toInt()/60.0))
        var speedInMph = (kmTravelled/1000) / hours
        var metValue = getMetForActivity(speedInMph)

        val constant = 3.5f

        var correctedMets = metValue * (constant / harrisBenedictRmR)
        return correctedMets * hours * weight
    }

    /**
     * Gets a users age from a date. Only takes into account years.
     *
     * @param age The date of birth.
     * @return The age in years.
     */


    fun convertKilocaloriesToMlKmin(kilocalories: Double, weightKgs: Double): Double {
        var kcalMin = kilocalories / 1440.0
        kcalMin /= 5.0

        return (kcalMin / weightKgs) * 1000.0
    }

    fun calculateDistanceTravelledInKM(stepsTaken: Int, entityStrideLength: Double): Double {
        return stepsTaken.toFloat() * entityStrideLength / 1000.0
    }

    /**
     * Gets the MET value for an activity. Based on https://sites.google.com/site/compendiumofphysicalactivities/Activity-Categories/walking .
     *
     * @param speedInMph The speed in miles per hour
     * @return The met value.
     */
    private fun getMetForActivity(speedInMph: Double): Float {
        if (speedInMph < 2.0) {
            return 2.0f
        } else if (java.lang.Float.compare(speedInMph.toFloat(), 2.0f) == 0) {
            return 2.8f
        } else if (java.lang.Float.compare(speedInMph.toFloat(), 2.0f) > 0 && java.lang.Float.compare(speedInMph.toFloat(), 2.7f) <= 0) {
            return 3.0f
        } else if (java.lang.Float.compare(speedInMph.toFloat(), 2.8f) > 0 && java.lang.Float.compare(speedInMph.toFloat(), 3.3f) <= 0) {
            return 3.5f
        } else if (java.lang.Float.compare(speedInMph.toFloat(), 3.4f) > 0 && java.lang.Float.compare(speedInMph.toFloat(), 3.5f) <= 0) {
            return 4.3f
        } else if (java.lang.Float.compare(speedInMph.toFloat(), 3.5f) > 0 && java.lang.Float.compare(speedInMph.toFloat(), 4.0f) <= 0) {
            return 5.0f
        } else if (java.lang.Float.compare(speedInMph.toFloat(), 4.0f) > 0 && java.lang.Float.compare(speedInMph.toFloat(), 4.5f) <= 0) {
            return 7.0f
        } else if (java.lang.Float.compare(speedInMph.toFloat(), 4.5f) > 0 && java.lang.Float.compare(speedInMph.toFloat(), 5.0f) <= 0) {
            return 8.3f
        } else if (java.lang.Float.compare(speedInMph.toFloat(), 5.0f) > 0) {
            return 9.8f
        }
        return 0f
    }

    /**
     * Calculates the Harris Benedict RMR value for an entity. Based on above calculation for Com
     *
     * @param gender   Users gender.
     * @param weightKg Weight in Kg.
     * @param age      Age in years.
     * @param heightCm Height in CM.
     * @return Harris benedictRMR value.
     */
    private fun harrisBenedictRmr(selectedId: Int, weightKg: Double, age: Int, heightCm: Double): Double {
        if (selectedId == R.id.rb_female_ex) {
            return 655.0955f + (1.8496f * heightCm) + (9.5634f * weightKg) - (4.6756f * age);

        } else {
            return 66.4730f + (5.0033f * heightCm) + (13.7516f * weightKg) - (6.7550f * age);
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
