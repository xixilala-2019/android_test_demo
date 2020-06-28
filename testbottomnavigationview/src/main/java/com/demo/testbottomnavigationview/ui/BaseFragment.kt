package com.demo.testbottomnavigationview.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

open class BaseFragment:Fragment() {

    fun log(log:String) {
        Log.e(BaseFragment@this.javaClass.simpleName +"  lifeCycle", "--$log")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        log("onAttach")
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        log("onActivityCreated")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")
    }

    override fun onDetach() {
        super.onDetach()
        log("onDetach")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log("onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        log("onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        log("onDestroy")
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        log("onPause")
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
    }
}