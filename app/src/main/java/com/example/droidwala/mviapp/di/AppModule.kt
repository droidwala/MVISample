package com.example.droidwala.mviapp.di

import android.app.Application
import android.content.Context
import com.example.droidwala.mviapp.data.source.Local
import com.example.droidwala.mviapp.data.source.Remote
import com.example.droidwala.mviapp.data.source.TasksDataSource
import com.example.droidwala.mviapp.data.source.local.TasksLocalDataSource
import com.example.droidwala.mviapp.data.source.remote.TasksRemoteDataSource
import com.example.droidwala.mviapp.util.schedulers.BaseSchedulerProvider
import com.example.droidwala.mviapp.util.schedulers.SchedulerProvider
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Created by punitdama on 07/01/18.
 */
@Module
abstract class AppModule{

    @Binds
    abstract fun bindContext(app: Application) : Context

    @Singleton
    @Binds
    abstract fun provideSchedulerProvider(provider : SchedulerProvider) : BaseSchedulerProvider


}