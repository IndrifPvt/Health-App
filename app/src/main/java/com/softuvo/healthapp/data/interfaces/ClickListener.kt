package com.softuvo.data.interfaces

import android.view.View
import java.util.ArrayList


interface ClickListener {
    fun onItemClicked(position: Int, view: View)
    fun onLongItemClicked(position: Int,view: View)
}