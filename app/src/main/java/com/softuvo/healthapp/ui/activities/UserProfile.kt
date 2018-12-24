package com.softuvo.healthapp.ui.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.softuvo.healthapp.R
import com.softuvo.healthapp.core.BaseActivity
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.BufferedReader
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.R.attr.name
import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseError
import java.nio.file.Files.exists
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.annotation.NonNull
import android.text.TextUtils
import android.util.Base64
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.*
import com.softuvo.healthapp.model.User
import com.softuvo.utils.AppConstants
import com.softuvo.utils.AppConstants.Companion.GALLERY_INTENT
import com.softuvo.utils.AppConstants.Companion.REQUEST_CAMERAINTENT
import com.softuvo.utils.CommonUtils
import com.theartofdev.edmodo.cropper.CropImage
import io.reactivex.internal.util.HalfSerializer.onComplete
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.dialog_photo_select.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*


class UserProfile : BaseActivity() {
    private var userChoosenTask: String? = null
    lateinit var db: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    private lateinit var user: FirebaseUser
    private  var users:User? = null
    private var uname=""
    private var uemail=""
    private var upassword=""
    private var uphone=""
    private var ulocation=""
    private var uage=""
    private var uheight_feet=""
    private var uheight_inch=""
    private var uweight=""
    private var mImageUri: Uri? = null
    private var picuri:Uri?=null
    private var contentURI:Uri?=null
    private lateinit var drawable: BitmapDrawable
    private var bytes:ByteArrayOutputStream?=null
    override fun onClick(v: View) {

        when (v.id) {
            R.id.btn_update -> {
               updateprofile()
            }
            R.id.iv_upload_image_button -> {
               selectphoto()
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
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

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        db = FirebaseDatabase.getInstance().getReference("User")
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        /*Glide.with(this *//* context *//*)
            .load("https://firebasestorage.googleapis.com/v0/b/fir-demo-361f5.appspot.com/o/images%2Fdpreet941%40gmail%2Ccom19%3A12%3A2018%2012%3A35%3A06?alt=media&token=4353c306-0e1d-4e4e-af8b-66a1f9c589fe")
            .into(iv_profile_image)*/
        user = auth.currentUser!!
        var login = db.child(auth.currentUser!!.email!!.replace(".",","))
        login.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val nuser = dataSnapshot.getValue(User::class.java)
                if (nuser!=null) {
                    et_userprofile__name.setText(nuser!!.username)
                    et_userprofile__location.setText(nuser!!.userlocation)
                    et_userprofile__email.setText(nuser!!.useremail)
                    et_userprofile__phone.setText(nuser!!.userphone)
                    et_userprofile__age.setText(nuser!!.userage)
                    et_userprofile__heightfeet.setText(nuser!!.userheight_feet)
                    et_userprofile__heightinch.setText(nuser!!.userheight_inch)
                    et_userprofile__weight.setText(nuser!!.userweight)
                    if (nuser.imageurl == null || nuser.imageurl == "") {

                    } else {
                        Glide.with(applicationContext)
                            .load(nuser.imageurl)
                            .into(iv_profile_image)
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
                else
                {
                    hideProgressDialog()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println(R.string.the_read + databaseError.code)
            }
        })
    }
    private fun updateprofile() {
        uname = et_userprofile__name.getText().toString().trim()
        uemail = et_userprofile__email.getText().toString().trim()
        uphone=et_userprofile__phone.text.toString().trim()
        ulocation=et_userprofile__location.text.toString().trim()
        uage=et_userprofile__age.text.toString().trim()
        uheight_feet=et_userprofile__heightfeet.text.toString().trim()
        uheight_inch=et_userprofile__heightinch.text.toString().trim()
        uweight=et_userprofile__weight.text.toString().trim()


        if (TextUtils.isEmpty(uname)) {
            Toast.makeText(applicationContext, R.string.enter_name, Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(uemail)) {
            Toast.makeText(applicationContext, R.string.enter_email, Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(uphone)) {
            Toast.makeText(applicationContext, R.string.enter_mobile, Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(ulocation)) {
            Toast.makeText(applicationContext, R.string.enter_location, Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(uage)) {
            Toast.makeText(applicationContext, R.string.enter_age, Toast.LENGTH_SHORT).show()
            return
        }
        if (TextUtils.isEmpty(uheight_feet)) {
            Toast.makeText(applicationContext, R.string.correct_height, Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(uheight_inch)) {
            Toast.makeText(applicationContext, R.string.correct_height, Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(uweight)) {
            Toast.makeText(applicationContext, R.string.correct_weight, Toast.LENGTH_SHORT).show()
            return
        }

        profile()
    }

    private fun profile() {
        showProgressDialog()

      /*drawable = iv_profile_image.getDrawable() as BitmapDrawable;
      var bitmap = drawable.getBitmap();*/
        val ref = storageReference.child("images/" + auth.currentUser!!.email!!.replace(".",","))
        /*ref.putFile(contentURI)
            .addOnSuccessListener {

                Toast.makeText(this@UserProfile, "Uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(this@UserProfile, "Failed " + e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = 100 * taskSnapshot.bytesTransferred / taskSnapshot
                    .totalByteCount
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressDialog.setProgress(progress.toInt(),true)

                }
            }*/
          if(contentURI!=null) {
              var nbitmap= MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
              var newbitmap=CommonUtils.compressImage(nbitmap)
              contentURI=CommonUtils.getImageUriFromBitmap(context,newbitmap)
              var uploadTask = contentURI?.let { ref.putFile(it) }

              val urlTask = uploadTask!!.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                  if (!task.isSuccessful) {
                      task.exception?.let {
                          throw it
                      }
                  }
                  return@Continuation ref.downloadUrl
              }).addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      hideProgressDialog()
                      var downloadUri = task.result!!
                      picuri = downloadUri
                      // var encodedImage = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
                      db.child(auth.currentUser!!.email!!.replace(".", ",")).child("username").setValue(uname)
                      db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userlocation").setValue(ulocation)
                      db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userphone").setValue(uphone)
                      db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userage").setValue(uage)
                      db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userheight_feet")
                          .setValue(uheight_feet)
                      db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userheight_inch")
                          .setValue(uheight_inch)
                      db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userweight").setValue(uweight)
                      db.child(auth.currentUser!!.email!!.replace(".", ",")).child("imageurl")
                          .setValue(picuri.toString())
                      finish()
                  } else {
                      // Handle failures
                      // ...
                  }
              }
          }
        else
          {
              hideProgressDialog()
              db.child(auth.currentUser!!.email!!.replace(".", ",")).child("username").setValue(uname)
              db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userlocation").setValue(ulocation)
              db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userphone").setValue(uphone)
              db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userage").setValue(uage)
              db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userheight_feet")
                  .setValue(uheight_feet)
              db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userheight_inch")
                  .setValue(uheight_inch)
              db.child(auth.currentUser!!.email!!.replace(".", ",")).child("userweight").setValue(uweight)
              finish()
          }
  }

  fun encodeToBase64(image: Bitmap, compressFormat: Bitmap.CompressFormat, quality: Int): String {
      val byteArrayOS = ByteArrayOutputStream()
      image.compress(compressFormat, quality, byteArrayOS)
      return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT)
  }

  private fun selectphoto() {
      val dialog = Dialog(context)
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.setContentView(R.layout.dialog_photo_select)
      dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      dialog.window!!.attributes.windowAnimations = R.style.animationName
      dialog.setCancelable(true)
      dialog.show()

      dialog.iv_camera.setOnClickListener(View.OnClickListener {
          userChoosenTask = getString(R.string.take_photo)
          permissionCheck(0, 0)
          dialog.dismiss()
      })
      dialog.iv_gallery.setOnClickListener(View.OnClickListener {
          userChoosenTask = getString(R.string.choose_photo)
          permissionCheck(1, 1)
          dialog.dismiss()
      })
      dialog.iv_cancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
  }
  private fun permissionCheck(check: Int, intentfor: Int) {
      when (check) {
          0 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              if (CommonUtils.requestAllPermissions(this, AppConstants.REQUEST_STORAGE_PERMISSION_CODE)) {
                  selectionIntent(intentfor)
              }
          } else {
              selectionIntent(intentfor)
          }
          1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              if (CommonUtils.requestAllPermissions(this, AppConstants.REQUEST_READ_STORAGE_PERMISSION_CODE)) {
                  selectionIntent(intentfor)
              }
          } else {
              selectionIntent(intentfor)
          }
      }
  }

  private fun selectionIntent(selected: Int) {
      val intent: Intent
      when (selected) {
          0 -> cameraIntent()
          1 -> {
              val galleryIntent = Intent(
                  Intent.ACTION_PICK,
                  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
              )

              startActivityForResult(galleryIntent, AppConstants.GALLERY_INTENT)
              overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
          }
      }
  }
  private fun cameraIntent() {
      try {
          /* val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
           startActivityForResult(intent, AppConstants.REQUEST_CAMERAINTENT)*/
         /* val values = ContentValues()
          values.put(MediaStore.Images.Media.TITLE, "New Picture")
          values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
          mImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
          val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
          startActivityForResult(intent, AppConstants.REQUEST_CAMERAINTENT)*/
          /*val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
          startActivityForResult(intent, AppConstants.REQUEST_CAMERAINTENT)
          overridePendingTransition(R.anim.slide_in, R.anim.slide_out)*/

          val values = ContentValues()
          values.put(MediaStore.Images.Media.TITLE, "New Picture")
          values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
          mImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
          val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
          startActivityForResult(intent, AppConstants.REQUEST_CAMERAINTENT)
          overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
      } catch (e: IOException) {
          e.printStackTrace()
      }
  }

  public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

      super.onActivityResult(requestCode, resultCode, data)


      if (requestCode == GALLERY_INTENT) {


          if (data != null) {

              try {
                  contentURI = data.data
                  val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                  iv_profile_image.setImageBitmap(bitmap)


              } catch (e: IOException) {
                  e.printStackTrace()
                  Toast.makeText(this@UserProfile, R.string.failed, Toast.LENGTH_SHORT).show()
              }

          }
          else
          {
              Toast.makeText(this@UserProfile,R.string.no_image,Toast.LENGTH_SHORT).show()
          }


      } else if (requestCode ==REQUEST_CAMERAINTENT ) {

              contentURI = mImageUri
          Glide.with(applicationContext)
              .load(contentURI)
              .into(iv_profile_image)



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
