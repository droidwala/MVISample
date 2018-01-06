package com.example.droidwala.mviapp.mvibase

import io.reactivex.Observable

/**
 * Created by punitdama on 06/01/18.
 */
interface MviViewModel<I : MviIntent, S: MviViewState>{

    fun viewState() : Observable<S>

    fun processIntents(intents : Observable<I>)
}