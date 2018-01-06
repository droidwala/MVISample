package com.example.droidwala.mviapp.data

import com.example.droidwala.mviapp.util.isNotNullNorEmpty
import com.example.droidwala.mviapp.util.isNullOrEmpty
import java.util.*

/**
 * Created by punitdama on 06/01/18.
 */
data class Task(
        val id : String = UUID.randomUUID().toString(),
        val title : String?,
        val description : String?,
        val completed : Boolean = false
){
    val titleForList =
            if(title.isNotNullNorEmpty()){
                title
            }
            else{
                description
            }

    val active = !completed
    val empty = title.isNullOrEmpty() && description.isNullOrEmpty()

}