package com.example.droidwala.mviapp.tasks

import com.example.droidwala.mviapp.data.Task
import com.xwray.groupie.GroupAdapter

/**
 * Created by punitdama on 06/01/18.
 */
class TasksAdapter : GroupAdapter(){

    fun addTasks(tasks : List<Task>){
        clear()
        for(task in tasks) {
            add(TaskItem(task))
        }
    }

    fun removeAllTasks(){
        clear()
    }
}