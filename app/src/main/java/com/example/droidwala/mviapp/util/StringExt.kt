package com.example.droidwala.mviapp.util

/**
 * Created by punitdama on 06/01/18.
 */
fun String?.isNullOrEmpty() = this == null || this.isEmpty()
fun String?.isNotNullNorEmpty() = !this.isNullOrEmpty()