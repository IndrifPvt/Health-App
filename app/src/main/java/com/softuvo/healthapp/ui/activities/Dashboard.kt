package com.softuvo.healthapp.ui.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.softuvo.healthapp.R
import com.softuvo.healthapp.core.BaseActivity
import com.softuvo.healthapp.data.prefs.PreferenceHandler
import com.softuvo.healthapp.model.*
import com.softuvo.utils.CommonUtils
import com.softuvo.utils.dialog.CustomAlertDialogListener
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.content_dashboard.*
import kotlinx.android.synthetic.main.nav_header_dashboard.view.*
import android.support.v4.widget.DrawerLayout
import android.widget.AdapterView



class Dashboard : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var menuItemWaiting:MenuItem?=null
    private var clickedNavItem=0
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_heart_rate -> {
                val intent = Intent(this@Dashboard, PulseCounter::class.java)
                startActivity(intent)

            }
            R.id.iv_bmr -> {
                val intent = Intent(this@Dashboard, BmrCalculator::class.java)
                startActivity(intent)
            }
            R.id.iv_excersise -> {
                val intent = Intent(this@Dashboard, ExcersiseBurn::class.java)
                startActivity(intent)
            }
            R.id.iv_calorie -> {
                val intent = Intent(this@Dashboard, CaloriesCounter::class.java)
                startActivity(intent)
            }
            R.id.iv_bmi -> {
                val intent = Intent(this@Dashboard, BMICalculator::class.java)
                startActivity(intent)
            }
            R.id.iv_step -> {
                var totalsteps=tv_step_count.text.toString().toInt()
                val intent = Intent(this@Dashboard, StepCounter::class.java)
                intent.putExtra("Steps",totalsteps)
                startActivity(intent)
            }
        }
    }
    private lateinit var authStateListener:FirebaseAuth.AuthStateListener
    lateinit var db: DatabaseReference
    lateinit var dbpulse: DatabaseReference
    lateinit var dbbmi: DatabaseReference
    lateinit var dbbmr: DatabaseReference
    lateinit var dbcalorie: DatabaseReference
    lateinit var dbexcersise: DatabaseReference
    lateinit var dbsteps:DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private lateinit var user: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setSupportActionBar(toolbar)
        showProgressDialog()
        db = FirebaseDatabase.getInstance().getReference("User")
        dbpulse = FirebaseDatabase.getInstance().getReference("Pulse")
        dbbmi = FirebaseDatabase.getInstance().getReference("BMI")
        dbbmr = FirebaseDatabase.getInstance().getReference("BMR")
        dbcalorie = FirebaseDatabase.getInstance().getReference("Calories")
        dbexcersise = FirebaseDatabase.getInstance().getReference("Excersise")
        dbsteps = FirebaseDatabase.getInstance().getReference("Step Count")
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        val header = nav_view.getHeaderView(0)
        user = auth.currentUser!!
        var login = db.child(auth.currentUser!!.email!!.replace(".",","))
        var pulse = dbpulse.child(auth.currentUser!!.email!!.replace(".",",")).child("lastbeat")
        var bmi = dbbmi.child(auth.currentUser!!.email!!.replace(".",",")).child("lastbmi")
        var bmr = dbbmr.child(auth.currentUser!!.email!!.replace(".",",")).child("lastbmr")
        var calorie = dbcalorie.child(auth.currentUser!!.email!!.replace(".",",")).child(CommonUtils.getCurrentDateTime()).child("Calorie Get")
        var excersise = dbexcersise.child(auth.currentUser!!.email!!.replace(".",",")).child("lastcal")
        var step= dbsteps.child(auth.currentUser!!.email!!.replace(".",",")).child(CommonUtils.getCurrentDateTime())

        step.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

               var steps = dataSnapshot.getValue(UserSteps::class.java)
                if(steps!=null)
                {
                    tv_step_count.setText(steps!!.steps)
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

        login.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val nuser = dataSnapshot.getValue(User::class.java)
                header.tv_profile_name.setText(nuser!!.username)
                if(nuser.imageurl==null || nuser.imageurl=="")
                {

                }
                else {
                    Glide.with(applicationContext)
                        .load(nuser.imageurl)
                        .into(header.iv_nav_profile_image)
                }
                /* if (nuser.imageurl=="" || nuser.imageurl==null)
                 {
                     iv_profile_image.setImageResource(R.drawable.ic_person_black_24dp)
                 }
                 else {
                     val decodedString = Base64.decode(nuser.imageurl, Base64.DEFAULT)
                     val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                     iv_profile_image.setImageBitmap(decodedByte)
                 }*/
                hideProgressDialog()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println(R.string.the_read + databaseError.code)
            }
        })

        pulse.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val nuser = dataSnapshot.getValue(UserPulse::class.java)
                if(nuser!=null)
                {
                    tv_heart_beat.setText(nuser!!.pulse)
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
        bmi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val nuser = dataSnapshot.getValue(UserData::class.java)
                if(nuser!=null)
                {
                    tv_bmi_count.setText(nuser!!.bmi)
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
        bmr.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val nuser = dataSnapshot.getValue(UserBmr::class.java)
                if(nuser!=null)
                {
                    tv_bmr_count.setText(nuser!!.bmr)
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
        calorie.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val nuser = dataSnapshot.getValue(UserEveryDayCalorie::class.java)
                if(nuser!=null)
                {
                    tv_cal_need.setText(nuser!!.totalcalories)
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
        excersise.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val nuser = dataSnapshot.getValue(UserExcersise::class.java)
                if(nuser!=null)
                {
                    tv_excersise.setText(nuser!!.excersise)
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


        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                hideProgressDialog()
                PreferenceHandler.writeBoolean(applicationContext, PreferenceHandler.IS_LOGGED_IN, false)
                val intent = Intent(this@Dashboard, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        auth.addAuthStateListener(authStateListener);

        /*val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )*/
        var drawerToggle = object : ActionBarDrawerToggle(this, drawer_layout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                when(clickedNavItem){
                    R.id.nav_profile -> {
                        clickedNavItem=0
                          val intent = Intent(this@Dashboard, UserProfile::class.java)
                         startActivity(intent)
                    }
                    R.id.nav_logout -> {
                        clickedNavItem=0
                        CommonUtils.showMessagePopup(context, resources.getString(R.string.logout_alert), resources.getString(R.string.logout_alert_msg), R.mipmap.info, clickListner, View.VISIBLE)

                    }
                }
            }

        }
        //drawer_layout.setDrawerListener(drawerToggle)

        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        /*drawer_layout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
             override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                if (menuItemWaiting != null) {
                    onNavigationItemSelected(menuItemWaiting!!)
                }
            }
        })*/

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

   /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }
*/
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

           // Handle navigation view item clicks here.
           when (item.itemId) {
               R.id.nav_profile -> {

                  clickedNavItem = R.id.nav_profile
                  // val intent = Intent(this@Dashboard, UserProfile::class.java)
                   // startActivity(intent)

               }
               R.id.nav_logout -> {

                   clickedNavItem = R.id.nav_logout
                   // CommonUtils.showMessagePopup(context, resources.getString(R.string.logout_alert), resources.getString(R.string.logout_alert_msg), R.mipmap.info, clickListner, View.VISIBLE)

               }
               R.id.nav_slideshow -> {

               }
               R.id.nav_manage -> {

               }
               R.id.nav_share -> {

               }
               R.id.nav_send -> {

               }
           }

       drawer_layout.closeDrawer(GravityCompat.START)
        return false
    }


    private fun signout() {
        auth.signOut()
    }
    private var clickListner: CustomAlertDialogListener = object : CustomAlertDialogListener {
        override fun OnClick(dialog: Dialog) {

        }

        override fun OnCallBackClick() {
            showProgressDialog()
            signout()
        }
    }



}
