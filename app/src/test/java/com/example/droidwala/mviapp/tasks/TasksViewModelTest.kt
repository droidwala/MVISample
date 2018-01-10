package com.example.droidwala.mviapp.tasks

import com.example.droidwala.mviapp.data.Task
import com.example.droidwala.mviapp.data.source.TasksRepository
import com.example.droidwala.mviapp.util.schedulers.BaseSchedulerProvider
import com.example.droidwala.mviapp.util.schedulers.ImmediateSchedulerProvider
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Created by punitdama on 07/01/18.
 */
class TasksViewModelTest{

    @Mock
    private lateinit var tasksRepository : TasksRepository
    private lateinit var schedulerProvider : BaseSchedulerProvider
    private lateinit var tasksViewModel : TasksViewModel
    private lateinit var testObserver : TestObserver<TasksViewState>
    private lateinit var tasks : List<Task>

    @Before
    fun setUpTasksViewModel(){
        MockitoAnnotations.initMocks(this)

        schedulerProvider = ImmediateSchedulerProvider()

        tasksViewModel = TasksViewModel(TasksActionProcessorHolder(tasksRepository,schedulerProvider))

        tasks = listOf(
                Task(title = "Title1", description = "Description1", completed = false),
                Task(title = "Title2", description = "Description2", completed = true),
                Task(title = "Title3", description = "Description3", completed = true)
        )

        testObserver = tasksViewModel.viewState().test()
    }

    @Test
    fun loadAllTasksFromRepositoryAndLoadIntoView(){
        `when`(tasksRepository.getTasks(any())).thenReturn(Single.just(tasks))

        tasksViewModel.processIntents(Observable.just(TasksIntent.InitialIntent))

        testObserver.assertValueAt(1,TasksViewState::isLoading)

        testObserver.assertValueAt(2,{taskViewState -> !taskViewState.isLoading && taskViewState.tasks.isNotEmpty()})
    }

    @Test
    fun loadActiveTasksFromRepositoryAndLoadIntoView(){
        `when`(tasksRepository.getTasks(any())).thenReturn(Single.just(tasks))

        tasksViewModel.processIntents(Observable.just(TasksIntent.ChangeFilterTypeIntent(TasksFilterType.ACTIVE_TASKS)))

        testObserver.assertValueAt(1,TasksViewState::isLoading)

        testObserver.assertValueAt(2,{taskViewState -> !taskViewState.isLoading && taskViewState.tasks.isNotEmpty() })
    }


    @Test
    fun loadCompletedTasksFromRepositoryAndLoadIntoView(){
        `when`(tasksRepository.getTasks(any())).thenReturn(Single.just(tasks))

        tasksViewModel.processIntents(Observable.just(TasksIntent.ChangeFilterTypeIntent(TasksFilterType.COMPLETED_TASKS)))

        testObserver.assertValueAt(1,TasksViewState::isLoading)

        testObserver.assertValueAt(2,{taskViewState -> !taskViewState.isLoading && taskViewState.tasks.isNotEmpty() })
    }

    @Test
    fun completeTask_ShowsTaskMarkedComplete(){
        val task = Task(title = "Title1",description = "Desc1")

        `when`(tasksRepository.completeTask(task)).thenReturn(Completable.complete())
        `when`(tasksRepository.getTasks()).thenReturn(Single.just(emptyList()))

        tasksViewModel.processIntents(Observable.just(TasksIntent.CompleteTaskIntent(task)))

        verify(tasksRepository).completeTask(task)
        verify(tasksRepository).getTasks()

        testObserver.assertValueAt(1,{taskViewState -> taskViewState.tasks.isEmpty()})
        testObserver.assertValueAt(2,TasksViewState::taskComplete)

    }


}