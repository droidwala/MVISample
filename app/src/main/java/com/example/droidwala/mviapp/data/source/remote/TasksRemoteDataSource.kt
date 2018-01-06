package com.example.droidwala.mviapp.data.source.remote

import com.example.droidwala.mviapp.data.Task
import com.example.droidwala.mviapp.data.source.TasksDataSource
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by punitdama on 06/01/18.
 */
@Singleton
class TasksRemoteDataSource @Inject constructor() : TasksDataSource{

    private val SERVICE_LATENCY_IN_MILLIS : Long = 5000
    private val tasksServiceData : MutableMap<String,Task>

    init {
        tasksServiceData = LinkedHashMap()
        addTask("Task1","Desc1")
        addTask("Task2","Desc2")
    }

    private fun addTask(title : String, description : String){
        val newTask = Task(title = title,description = description)
        tasksServiceData.put(newTask.id,newTask)
    }

    override fun getTasks(): Single<List<Task>> {
        return Observable.fromIterable(tasksServiceData.values)
                .delay(SERVICE_LATENCY_IN_MILLIS,TimeUnit.SECONDS)
                .toList()
    }

    override fun getTask(taskId: String): Single<Task> {
        return Single.just<Task>(tasksServiceData[taskId])
                .delay(SERVICE_LATENCY_IN_MILLIS,TimeUnit.SECONDS)
    }

    override fun saveTask(task: Task): Completable {
        tasksServiceData.put(task.id,task)
        return Completable.complete()
    }

    override fun completeTask(task: Task): Completable {
        val newTask = Task(task.id,task.title,task.description,true)
        tasksServiceData.put(task.id,newTask)
        return Completable.complete()
    }


    override fun completeTask(taskId: String): Completable {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
        return Completable.complete()
    }

    override fun activateTask(task: Task): Completable {
        val activeTask = Task(title = task.title!!, description = task.description!!, id = task.id)
        tasksServiceData.put(task.id, activeTask)
        return Completable.complete()
    }

    override fun activateTask(taskId: String): Completable {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
        return Completable.complete()
    }

    override fun clearCompletedTasks(): Completable {
        tasksServiceData.filterValues { task -> task.completed }
                .forEach{(taskId,_) -> tasksServiceData.remove(taskId)}

        return Completable.complete()
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllTasks() {
        tasksServiceData.clear()
    }

    override fun deleteTask(taskId: String): Completable {
        tasksServiceData.remove(taskId)
        return Completable.complete()
    }

}