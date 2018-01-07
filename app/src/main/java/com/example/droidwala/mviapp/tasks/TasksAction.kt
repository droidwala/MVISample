package com.example.droidwala.mviapp.tasks

import com.example.droidwala.mviapp.data.Task
import com.example.droidwala.mviapp.mvibase.MviAction

/**
 * Created by punitdama on 06/01/18.
 */
sealed class TasksAction : MviAction{

    data class LoadTasksAction(val forceUpdate : Boolean,
                               val filterType: TasksFilterType? = TasksFilterType.ALL_TASKS)
        : TasksAction()

    data class ActivateTaskAction(val task : Task) : TasksAction()

    data class CompleteTaskAction(val task : Task) : TasksAction()

    object ClearCompletedTasksAction : TasksAction()
}