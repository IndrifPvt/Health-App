package com.softuvo.mgc.data.interfaces

import android.view.View

/*
 * Created by softuvo on 30/1/18.
 */

interface InterestCheckedChangeListener {
    fun onCheckedChange(buttonView: View,boolean: Boolean,pos:Int,id:String)

}