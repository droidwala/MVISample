package com.example.droidwala.mviapp.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity

/**
 * Created by punitdama on 07/01/18.
 */
inline fun <reified VM : ViewModel> FragmentActivity.viewModelProvider(
        mode : LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
        crossinline provider : () -> VM) = lazy(mode){
    ViewModelProviders.of(this,object : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>) = provider() as T
    }).get(VM::class.java)
}
