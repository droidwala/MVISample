package com.example.droidwala.mviapp.data.source.remote

import android.support.annotation.VisibleForTesting
import com.example.droidwala.mviapp.data.Task
import com.example.droidwala.mviapp.data.source.TasksDataSource
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.LinkedHashMap

/**
 * Created by punitdama on 07/01/18.
 */
@Singleton
class FakeTasksRemoteDataSource @Inject constructor() : TasksDataSource{

    private val tasksServiceData: MutableMap<String, Task>

    init {
        tasksServiceData = LinkedHashMap<String,Task>()
        addTask("Task1","Desc1")
        addTask("Task2","Desc2")
    }

    private fun addTask(title : String, description : String){
        val newTask = Task(title = title,description = description)
        tasksServiceData.put(newTask.id,newTask)
    }

    override fun getTasks(): Single<List<Task>> {
        return Observable.fromIterable(tasksServiceData.values).toList()
    }

    override fun getTask(taskId: String): Single<Task> {
        return Single.just(tasksServiceData[taskId])
    }

    override fun saveTask(task: Task): Completable {
        tasksServiceData.put(task.id, task)
        return Completable.complete()
    }

    override fun completeTask(task: Task): Completable {
        val completedTask = Task(task.title!!, task.description, task.id, true)
        tasksServiceData.put(task.id, completedTask)
        return Completable.complete()
    }

    override fun completeTask(taskId: String): Completable {
        val task = tasksServiceData[taskId]!!
        val completedTask = Task(task.title!!, task.description, task.id, true)
        tasksServiceData.put(taskId, completedTask)
        return Completable.complete()
    }

    override fun activateTask(task: Task): Completable {
        val activeTask = Task(title = task.title!!, description = task.description!!, id = task.id)
        tasksServiceData.put(task.id, activeTask)
        return Completable.complete()
    }

    override fun activateTask(taskId: String): Completable {
        val task = tasksServiceData[taskId]!!
        val activeTask = Task(title = task.title!!, description = task.description!!, id = task.id)
        tasksServiceData.put(taskId, activeTask)
        return Completable.complete()
    }

    override fun clearCompletedTasks(): Completable {
        val it = tasksServiceData.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next()
            if (entry.value.completed) {
                it.remove()
            }
        }
        return Completable.complete()
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteTask(taskId: String): Completable {
        tasksServiceData.remove(taskId)
        return Completable.complete()
    }

    override fun deleteAllTasks() {
        tasksServiceData.clear()
    }

    @VisibleForTesting
    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData.put(task.id, task)
        }
    }
}