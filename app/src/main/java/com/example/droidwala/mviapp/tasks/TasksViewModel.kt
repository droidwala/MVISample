package com.example.droidwala.mviapp.tasks

import android.arch.lifecycle.ViewModel
import com.example.droidwala.mviapp.data.Task
import com.example.droidwala.mviapp.mvibase.MviViewModel
import com.example.droidwala.mviapp.tasks.TasksAction.*
import com.example.droidwala.mviapp.tasks.TasksResult.*
import com.example.droidwala.mviapp.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by punitdama on 06/01/18.
 */
class TasksViewModel @Inject constructor(val tasksActionProcessorHolder: TasksActionProcessorHolder)
    : ViewModel(),MviViewModel<TasksIntent,TasksViewState>{

    private val intentsSubject : PublishSubject<TasksIntent> = PublishSubject.create();
    private val statesObservable : Observable<TasksViewState> = compose();


    override fun processIntents(intents: Observable<TasksIntent>) {
        intents.subscribe(intentsSubject)
    }

    override fun viewState(): Observable<TasksViewState>  = statesObservable

    private val intentFilter : ObservableTransformer<TasksIntent,TasksIntent>
        get() = ObservableTransformer { intents ->
            intents.publish{shared ->
                Observable.merge(
                        shared.ofType(TasksIntent.InitialIntent::class.java).take(1),
                        shared.notOfType(TasksIntent.InitialIntent::class.java)
                )
            }
        }


    private fun compose() : Observable<TasksViewState>{
        return intentsSubject
                    .compose(intentFilter)
                    .map(this::actionFromIntent)
                    .compose(tasksActionProcessorHolder.actionProcessor)
                    .scan(TasksViewState.idle(),reducer)
                    .replay(1)
                    .autoConnect(0)
    }


    private fun actionFromIntent(intent: TasksIntent) : TasksAction{
        return when(intent){
            is TasksIntent.InitialIntent -> LoadTasksAction(false)
            is TasksIntent.RefreshIntent -> LoadTasksAction(true)
            is TasksIntent.ActivateTaskIntent -> ActivateTaskAction(intent.task)
            is TasksIntent.CompleteTaskIntent -> CompleteTaskAction(intent.task)
            is TasksIntent.ClearCompletedTaskIntent -> ClearCompletedTasksAction
            is TasksIntent.ChangeFilterTypeIntent -> LoadTasksAction(false,intent.filterType)
        }
    }


    companion object {
        private val reducer = {previousState : TasksViewState, result : TasksResult ->
            when(result){
                is LoadTasksResult -> when(result){
                    is LoadTasksResult.Success -> {
                        val filterType = result.filterType ?: previousState.tasksFilterType
                        val tasks = filteredTasks(result.tasks,filterType)
                        previousState.copy(
                                isLoading = false,
                                tasks =  tasks,
                                tasksFilterType = filterType)
                    }
                    is LoadTasksResult.Failure -> previousState.copy(isLoading = false,error = result.error)
                    is LoadTasksResult.InFlight -> previousState.copy(isLoading = true)
                }

                is CompleteTaskResult -> when (result) {
                    is CompleteTaskResult.Success ->
                        previousState.copy(
                                taskComplete = true,
                                tasks = filteredTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is CompleteTaskResult.Failure -> previousState.copy(error = result.error)
                    is CompleteTaskResult.InFlight -> previousState
                    is CompleteTaskResult.HideUiNotification -> previousState.copy(taskComplete = false)
                }
                is ActivateTaskResult -> when (result) {
                    is ActivateTaskResult.Success ->
                        previousState.copy(
                                taskActivated = true,
                                tasks = filteredTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is ActivateTaskResult.Failure -> previousState.copy(error = result.error)
                    is ActivateTaskResult.InFlight -> previousState
                    is ActivateTaskResult.HideUiNotification -> previousState.copy(taskActivated = false)
                }
                is ClearCompletedTasksResult -> when (result) {
                    is ClearCompletedTasksResult.Success ->
                        previousState.copy(
                                completedTasksCleared = true,
                                tasks = filteredTasks(result.tasks, previousState.tasksFilterType)
                        )
                    is ClearCompletedTasksResult.Failure -> previousState.copy(error = result.error)
                    is ClearCompletedTasksResult.InFlight -> previousState
                    is ClearCompletedTasksResult.HideUiNotification ->
                        previousState.copy(completedTasksCleared = false)
                }

            }

        }

        private fun filteredTasks(tasks: List<Task>, filterType: TasksFilterType): List<Task> {
            return when (filterType) {
                TasksFilterType.ALL_TASKS -> tasks
                TasksFilterType.ACTIVE_TASKS -> tasks.filter(Task::active)
                TasksFilterType.COMPLETED_TASKS -> tasks.filter(Task::completed)
            }
        }
    }




}