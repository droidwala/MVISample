package com.example.droidwala.mviapp.di

import com.example.droidwala.mviapp.tasks.TasksActivity
import com.example.droidwala.mviapp.tasks.TasksModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by punitdama on 07/01/18.
 */
@Module
abstract class ActivityBindingModule{

    @ActivityScope
    @ContributesAndroidInjector(modules = arrayOf(TasksModule::class))
    abstract fun tasksActivity(): TasksActivity
}