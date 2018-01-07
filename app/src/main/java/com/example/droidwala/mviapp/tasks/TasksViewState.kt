package com.example.droidwala.mviapp.tasks

import com.example.droidwala.mviapp.data.Task
import com.example.droidwala.mviapp.mvibase.MviViewState

/**
 * Created by punitdama on 06/01/18.
 */
data class TasksViewState(
        val isLoading : Boolean,
        val tasksFilterType : TasksFilterType,
        val tasks : List<Task>,
        val error : Throwable?,
        val taskComplete : Boolean,
        val taskActivated : Boolean,
        val completedTasksCleared : Boolean
) : MviViewState{
    
    companion object {
        fun idle() : TasksViewState{
            return TasksViewState(
                    isLoading = false,
                    tasksFilterType = TasksFilterType.ALL_TASKS,
                    tasks = emptyList(),
                    error = null,
                    taskComplete = false,
                    taskActivated = false,
                    completedTasksCleared = false)
        }
    }
}

