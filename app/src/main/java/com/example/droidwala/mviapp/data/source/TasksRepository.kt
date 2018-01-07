package com.example.droidwala.mviapp.data.source

import com.example.droidwala.mviapp.data.Task
import com.example.droidwala.mviapp.data.source.local.TasksLocalDataSource
import com.example.droidwala.mviapp.data.source.remote.TasksRemoteDataSource
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by punitdama on 06/01/18.
 */
@Singleton
class TasksRepository @Inject constructor(
        val tasksRemoteDataSource: TasksRemoteDataSource,
        val tasksLocalDataSource: TasksLocalDataSource)
    : TasksDataSource{


    var cachedTasks : MutableMap<String, Task> = mutableMapOf()

    var cacheIsDirty = false

    private fun getAndCacheLocalTasks() : Single<List<Task>>{
        return tasksLocalDataSource.getTasks()
                .flatMap{ tasks ->
                    Observable.fromIterable(tasks)
                            .doOnNext { task -> cachedTasks.put(task.id,task)}
                            .toList()
                }
    }

    private fun getAndSaveRemoteTasks() : Single<List<Task>>{
        return tasksRemoteDataSource.getTasks()
                .flatMap { tasks ->
                    Observable.fromIterable(tasks).doOnNext { task ->
                        tasksLocalDataSource.saveTask(task);
                        cachedTasks.put(task.id,task)
                    }.toList()
                }
                .doOnSuccess { cacheIsDirty = false }

    }

    override fun getTasks(): Single<List<Task>> {
        if(cachedTasks.isNotEmpty() && !cacheIsDirty){
            return Observable.fromIterable(cachedTasks.values).toList()
        }

        val remoteTasks = getAndSaveRemoteTasks()
        return if(cacheIsDirty){
            remoteTasks
        }
        else{
            val localTasks = getAndCacheLocalTasks()
            Single.concat(localTasks,remoteTasks)
                    .filter { tasks -> !tasks.isEmpty() }
                    .firstOrError()
        }
    }

    override fun saveTask(task: Task): Completable {
        tasksLocalDataSource.saveTask(task)
        tasksRemoteDataSource.saveTask(task)

        cachedTasks.put(task.id,task)

        return Completable.complete()
    }

    override fun completeTask(task: Task): Completable {
        tasksLocalDataSource.completeTask(task)
        tasksRemoteDataSource.completeTask(task)

        val completedTask = Task(task.id,task.title,task.description,true)
        cachedTasks.put(completedTask.id,completedTask)

        return Completable.complete()
    }

    override fun completeTask(taskId: String): Completable {
        val task = getTaskWithId(taskId)

        return if(task !=null){
            completeTask(task)
        } else{
            return Completable.complete()
        }
    }

    override fun activateTask(task: Task): Completable {
        tasksRemoteDataSource.activateTask(task)
        tasksLocalDataSource.activateTask(task)

        val activeTask =
                Task(title = task.title!!, description = task.description, id = task.id, completed = false)

        // Do in memory cache update to keep the app UI up to date
        if (cachedTasks == null) {
            cachedTasks = LinkedHashMap()
        }
        cachedTasks!!.put(task.id, activeTask)
        return Completable.complete()
    }

    override fun activateTask(taskId: String): Completable {
        val taskWithId = getTaskWithId(taskId)
        return if (taskWithId != null) {
            activateTask(taskWithId)
        } else {
            Completable.complete()
        }
    }

    override fun deleteTask(taskId: String): Completable {
        tasksRemoteDataSource.deleteTask(checkNotNull(taskId))
        tasksLocalDataSource.deleteTask(checkNotNull(taskId))

        cachedTasks.remove(taskId)
        return Completable.complete()
    }

    override fun clearCompletedTasks(): Completable {
        tasksRemoteDataSource.clearCompletedTasks()
        tasksLocalDataSource.clearCompletedTasks()

        val it = cachedTasks.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next()
            if (entry.value.completed) {
                it.remove()
            }
        }
        return Completable.complete()
    }

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     */
    override fun getTask(taskId: String): Single<Task> {
        val cachedTask = getTaskWithId(taskId)

        // Respond immediately with cache if available
        if (cachedTask != null) {
            return Single.just(cachedTask)
        }

        // LoadAction from server/persisted if needed.


        // Is the task in the local data source? If not, query the network.
        val localTask = getTaskWithIdFromLocalRepository(taskId)
        val remoteTask = tasksRemoteDataSource.getTask(taskId)
                .doOnSuccess { task ->
                    tasksLocalDataSource.saveTask(task)
                    cachedTasks.put(task.id, task)
                }

        return Single.concat(localTask, remoteTask).firstOrError()
    }

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks()
        tasksLocalDataSource.deleteAllTasks()

        cachedTasks.clear()
    }

    fun getTaskWithIdFromLocalRepository(taskId: String): Single<Task> {
        return tasksLocalDataSource.getTask(taskId)
                .doOnSuccess { task -> cachedTasks.put(taskId, task) }
    }



    private fun getTaskWithId(taskId : String) : Task? = cachedTasks.get(taskId)
}