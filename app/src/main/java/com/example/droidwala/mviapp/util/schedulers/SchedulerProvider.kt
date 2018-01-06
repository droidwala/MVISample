package com.example.droidwala.mviapp.util.schedulers

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by punitdama on 06/01/18.
 */
class SchedulerProvider : BaseSchedulerProvider{

    override fun ui()  = AndroidSchedulers.mainThread();

    override fun io() = Schedulers.io()

    override fun computation() = Schedulers.computation()

}