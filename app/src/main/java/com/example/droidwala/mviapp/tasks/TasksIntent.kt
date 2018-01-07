package com.example.droidwala.mviapp.tasks

import com.example.droidwala.mviapp.data.Task
import com.example.droidwala.mviapp.mvibase.MviIntent

/**
 * Created by punitdama on 06/01/18.
 */
sealed class TasksIntent : MviIntent{
    object InitialIntent : TasksIntent()

    data class RefreshIntent(val forceUpdate : Boolean) : TasksIntent()

    data class ActivateTaskIntent(val task : Task) : TasksIntent()

    data class CompleteTaskIntent(val task: Task) : TasksIntent()

    object ClearCompletedTaskIntent : TasksIntent()

    data class ChangeFilterTypeIntent(val filterType: TasksFilterType) : TasksIntent()
}