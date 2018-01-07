package com.example.droidwala.mviapp.di

import android.app.Application
import com.example.droidwala.mviapp.TasksApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

/**
 * Created by punitdama on 07/01/18.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class,
        TasksRepositoryModule::class,
        ActivityBindingModule::class,
        AndroidInjectionModule::class))
interface AppComponent : AndroidInjector<TasksApplication>{

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application : Application) : AppComponent.Builder

        fun build() : AppComponent
    }

}