package com.example.droidwala.mviapp.di

import com.example.droidwala.mviapp.data.source.Local
import com.example.droidwala.mviapp.data.source.Remote
import com.example.droidwala.mviapp.data.source.TasksDataSource
import com.example.droidwala.mviapp.data.source.local.TasksLocalDataSource
import com.example.droidwala.mviapp.data.source.remote.TasksRemoteDataSource
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
    abstract fun provideRemoteDataSource(datasource : TasksRemoteDataSource) : TasksDataSource

    @Singleton
    @Binds
    @Local
    abstract fun provideLocalDataSource(datasource: TasksLocalDataSource) : TasksDataSource


}