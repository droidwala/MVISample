package com.example.droidwala.mviapp.tasks

import android.arch.lifecycle.ViewModel
import com.example.droidwala.mviapp.di.ActivityScope
import dagger.Binds
import dagger.Module

/**
 * Created by punitdama on 07/01/18.
 */
@Module
abstract class TasksModule{

    @ActivityScope
    @Binds
    abstract fun viewModel(viewModel: TasksViewModel) : ViewModel
}