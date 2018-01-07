package com.example.droidwala.mviapp.util.schedulers

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by punitdama on 06/01/18.
 */
@Singleton
class SchedulerProvider @Inject constructor() : BaseSchedulerProvider{

    override fun ui()  = AndroidSchedulers.mainThread();

    override fun io() = Schedulers.io()

    override fun computation() = Schedulers.computation()

}