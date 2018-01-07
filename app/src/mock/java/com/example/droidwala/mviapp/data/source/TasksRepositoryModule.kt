package com.example.droidwala.mviapp.data.source

import com.example.droidwala.mviapp.data.source.local.TasksLocalDataSource
import com.example.droidwala.mviapp.data.source.remote.FakeTasksRemoteDataSource
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Created by punitdama on 07/01/18.
 */
@Module
abstract class TasksRepositoryModule{

    @Singleton
    @Binds
    @Remote
    abstract fun provideRemoteDataSource(datasource : FakeTasksRemoteDataSource) : TasksDataSource

    @Singleton
    @Binds
    @Local
    abstract fun provideLocalDataSource(datasource: TasksLocalDataSource) : TasksDataSource


}