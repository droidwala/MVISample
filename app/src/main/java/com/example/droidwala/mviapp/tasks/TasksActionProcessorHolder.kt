package com.example.droidwala.mviapp.tasks

import com.example.droidwala.mviapp.data.source.TasksRepository
import com.example.droidwala.mviapp.util.schedulers.BaseSchedulerProvider
import io.reactivex.ObservableTransformer
import com.example.droidwala.mviapp.tasks.TasksAction.*
import com.example.droidwala.mviapp.tasks.TasksResult.*
import com.example.droidwala.mviapp.util.pairWithDelay
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by punitdama on 06/01/18.
 */
class TasksActionProcessorHolder @Inject constructor(
        val tasksRepository: TasksRepository,
        val schedulerProvider: BaseSchedulerProvider){

    private val loadTaskProcessor =
            ObservableTransformer<LoadTasksAction,LoadTasksResult>{ actions ->
                actions.flatMap { action ->
                    tasksRepository.getTasks(action.forceUpdate)
                            .toObservable()
                            .map { tasks -> LoadTasksResult.Success(tasks,action.filterType) }
                            .cast(LoadTasksResult::class.java)
                            .onErrorReturn(LoadTasksResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .startWith(LoadTasksResult.InFlight)
                }

            }

    private val activateTaskProcessor =
            ObservableTransformer<ActivateTaskAction,ActivateTaskResult> { actions ->
                actions.flatMap { action ->
                    tasksRepository.completeTask(action.task)
                            .andThen(tasksRepository.getTasks())
                            .toObservable()
                            .flatMap { tasks ->
                                pairWithDelay(
                                        ActivateTaskResult.Success(tasks),
                                        ActivateTaskResult.HideUiNotification)
                            }
                            .onErrorReturn(ActivateTaskResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .startWith(ActivateTaskResult.InFlight)

                }
            }

    private val completeTaskProcessor =
            ObservableTransformer<CompleteTaskAction, CompleteTaskResult> { actions ->
                actions.flatMap { action ->
                    tasksRepository.completeTask(action.task)
                            .andThen(tasksRepository.getTasks())
                            // Transform the Single to an Observable to allow emission of multiple
                            // events down the stream (e.g. the InFlight event)
                            .toObservable()
                            .flatMap { tasks ->
                                // Emit two events to allow the UI notification to be hidden after
                                // some delay
                                pairWithDelay(
                                        TasksResult.CompleteTaskResult.Success(tasks),
                                        TasksResult.CompleteTaskResult.HideUiNotification)
                            }
                            // Wrap any error into an immutable object and pass it down the stream
                            // without crashing.
                            // Because errors are data and hence, should just be part of the stream.
                            .onErrorReturn(CompleteTaskResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            // Emit an InFlight event to notify the subscribers (e.g. the UI) we are
                            // doing work and waiting on a response.
                            // We emit it after observing on the UI thread to allow the event to be emitted
                            // on the current frame and avoid jank.
                            .startWith(TasksResult.CompleteTaskResult.InFlight)
                }
            }

    private val clearCompletedTasksProcessor =
            ObservableTransformer<ClearCompletedTasksAction, ClearCompletedTasksResult> { actions ->
                actions.flatMap {
                    tasksRepository.clearCompletedTasks()
                            .andThen(tasksRepository.getTasks())
                            // Transform the Single to an Observable to allow emission of multiple
                            // events down the stream (e.g. the InFlight event)
                            .toObservable()
                            .flatMap { tasks ->
                                // Emit two events to allow the UI notification to be hidden after
                                // some delay
                                pairWithDelay(
                                        ClearCompletedTasksResult.Success(tasks),
                                        ClearCompletedTasksResult.HideUiNotification)
                            }
                            // Wrap any error into an immutable object and pass it down the stream
                            // without crashing.
                            // Because errors are data and hence, should just be part of the stream.
                            .onErrorReturn(ClearCompletedTasksResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            // Emit an InFlight event to notify the subscribers (e.g. the UI) we are
                            // doing work and waiting on a response.
                            // We emit it after observing on the UI thread to allow the event to be emitted
                            // on the current frame and avoid jank.
                            .startWith(TasksResult.ClearCompletedTasksResult.InFlight)
                }
            }


    internal var actionProcessor =
            ObservableTransformer<TasksAction,TasksResult> { actions ->
                actions.publish { shared ->
                    Observable.merge(
                            shared.ofType(TasksAction.LoadTasksAction::class.java).compose(loadTaskProcessor),
                            shared.ofType(TasksAction.ActivateTaskAction::class.java).compose(activateTaskProcessor),
                            shared.ofType(TasksAction.CompleteTaskAction::class.java).compose(completeTaskProcessor),
                            shared.ofType(TasksAction.ClearCompletedTasksAction::class.java).compose(clearCompletedTasksProcessor)
                    ).mergeWith(
                            shared.filter { v ->
                                v !is TasksAction.LoadTasksAction
                                        && v !is TasksAction.ActivateTaskAction
                                        && v !is TasksAction.CompleteTaskAction
                                        && v !is TasksAction.ClearCompletedTasksAction
                            }.flatMap { w ->
                                Observable.error<TasksResult>(
                                        IllegalArgumentException("Unknown Action type: $w"))
                            }
                    )

                }
            }
}