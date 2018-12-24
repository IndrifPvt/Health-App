package com.softuvo.healthapp.core

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.softuvo.utils.CallProgressWheel
import com.softuvo.utils.CommonUtils
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment  : Fragment() {
    lateinit var mContext: Context
    lateinit var rootview: View
    val compositeDrawable: CompositeDisposable = CompositeDisposable()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootview = inflater.inflate(getLayoutId(), container, false)
        mContext = activity!!.applicationContext
        return rootview
    }

    abstract fun getLayoutId(): Int;
    abstract fun onClick(v: View);

    fun isNetworkConnected(): Boolean {
        return CommonUtils.isInternetConnection(mContext)
    }

    fun showProgressDialog() {
        CallProgressWheel.showLoadingDialog(mContext)
    }

    fun hideProgressDialog() {
        CallProgressWheel.dismissLoadingDialog()
    }

    override fun onDestroy() {
        hideProgressDialog()
        compositeDrawable.clear()
        super.onDestroy()
    }
}