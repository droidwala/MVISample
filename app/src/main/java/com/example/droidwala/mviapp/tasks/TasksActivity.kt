package com.example.droidwala.mviapp.tasks

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.droidwala.mviapp.R
import com.example.droidwala.mviapp.mvibase.MviView
import com.example.droidwala.mviapp.util.viewModelProvider
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_tasks.*
import kotlinx.android.synthetic.main.toolbar_with_title.*
import javax.inject.Inject

/**
 * Created by punitdama on 06/01/18.
 */
class TasksActivity : AppCompatActivity(),MviView<TasksIntent,TasksViewState>{

    @Inject lateinit var vm : TasksViewModel

    private lateinit var adapter : TasksAdapter
    private val viewModel by viewModelProvider { vm }
    private val disposables = CompositeDisposable()

    private val refreshIntentPublisher =  PublishSubject.create<TasksIntent.RefreshIntent>()
    private val clearCompletedTaskIntentPublisher = PublishSubject.create<TasksIntent.ClearCompletedTaskIntent>()
    private val changeFilterIntentPublisher = PublishSubject.create<TasksIntent.ChangeFilterTypeIntent>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_tasks)
        setUpToolbar();
        adapter = TasksAdapter()
        rv.adapter = adapter

        bindViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindViewModel()
    }

    private fun bindViewModel(){
        disposables += viewModel.viewState().subscribe(this::render)
        viewModel.processIntents(intents())

    }

    private fun unbindViewModel(){
        disposables.dispose()
    }


    override fun render(state: TasksViewState) {
        swipe_refresh_layout.isRefreshing = state.isLoading

        current_filter_label.text = state.tasksFilterType.name

        if(state.error !=null){
            showLoadingTasksError()
            return
        }

        if(state.taskActivated) showMessage(getString(R.string.task_marked_active))
        if(state.taskComplete) showMessage(getString(R.string.task_marked_complete))


        if(state.tasks.isEmpty()){
            no_tasks_left.visibility = View.VISIBLE
            adapter.removeAllTasks()

            when(state.tasksFilterType){
                TasksFilterType.ACTIVE_TASKS -> showNoActiveTasks()
                TasksFilterType.COMPLETED_TASKS -> showNoCompletedTasks()
                else -> showNoTasks();
            }
        }
        else{
            no_tasks_left.visibility = View.GONE
            adapter.addTasks(state.tasks)
        }

    }

    override fun intents(): Observable<TasksIntent> {
        return Observable.merge(
                initialIntent(),
                refreshIntent(),
                clearCompletedTaskIntent()).mergeWith(
                changeFilterIntent())
    }

    private fun initialIntent() : Observable<TasksIntent.InitialIntent>{
        return Observable.just(TasksIntent.InitialIntent)
    }

    private fun refreshIntent() : Observable<TasksIntent.RefreshIntent>{
        return RxSwipeRefreshLayout.refreshes(swipe_refresh_layout)
                .map { TasksIntent.RefreshIntent(true) }
                .mergeWith(refreshIntentPublisher)
    }

    private fun clearCompletedTaskIntent() : Observable<TasksIntent.ClearCompletedTaskIntent>{
        return clearCompletedTaskIntentPublisher
    }

    private fun changeFilterIntent() : Observable<TasksIntent.ChangeFilterTypeIntent>{
        return changeFilterIntentPublisher
    }



    private fun showMessage(message : String){
        Snackbar.make(rv,message,Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoadingTasksError(){
        showMessage(getString(R.string.loading_tasks_error))
    }

    private fun showNoActiveTasks(){
        no_tasks_left.text = getString(R.string.no_tasks_active)
    }

    private fun showNoCompletedTasks(){
        no_tasks_left.text = getString(R.string.no_tasks_completed)
    }

    private fun showNoTasks(){
        no_tasks_left.text = getString(R.string.no_tasks_left)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.filter_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.all_tasks ->
                changeFilterIntentPublisher.onNext(TasksIntent.ChangeFilterTypeIntent(TasksFilterType.ALL_TASKS))
            R.id.active_tasks ->
                changeFilterIntentPublisher.onNext(TasksIntent.ChangeFilterTypeIntent(TasksFilterType.ACTIVE_TASKS))
            R.id.completed_tasks ->
                changeFilterIntentPublisher.onNext(TasksIntent.ChangeFilterTypeIntent(TasksFilterType.COMPLETED_TASKS))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowTitleEnabled(false)
        }
    }
}