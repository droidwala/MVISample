package com.example.droidwala.mviapp.util.schedulers

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * Created by punitdama on 07/01/18.
 */
class ImmediateSchedulerProvider : BaseSchedulerProvider{

    override fun ui() = Schedulers.trampoline()

    override fun io() = Schedulers.trampoline()

    override fun computation() = Schedulers.trampoline()

}