package com.example.droidwala.mviapp.util.schedulers

import io.reactivex.Scheduler

/**
 * Created by punitdama on 06/01/18.
 */
interface BaseSchedulerProvider{

    fun ui() : Scheduler

    fun io() : Scheduler

    fun computation() : Scheduler
}