package com.example.droidwala.mviapp.util

import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by punitdama on 07/01/18.
 */
fun CompositeDisposable.plusAssign(disposable: Disposable){
    add(disposable)
}

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
fun <T : Any, U : Any> Observable<T>.notOfType(clazz: Class<U>): Observable<T> {
    checkNotNull(clazz) { "clazz is null" }
    return filter { !clazz.isInstance(it) }
}

fun <T> pairWithDelay(immediate: T, delayed: T): Observable<T> {
    return Observable.timer(2, TimeUnit.SECONDS)
            .map { delayed }
            .startWith(immediate)
}
