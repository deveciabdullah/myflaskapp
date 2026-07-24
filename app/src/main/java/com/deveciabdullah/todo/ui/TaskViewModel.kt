package com.deveciabdullah.todo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.deveciabdullah.todo.TodoApplication
import com.deveciabdullah.todo.data.Task
import com.deveciabdullah.todo.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter { ALL, ACTIVE, COMPLETED }

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val activeCount: Int = 0,
    val completedCount: Int = 0,
)

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val filter = MutableStateFlow(TaskFilter.ALL)

    /** Most recently deleted task, kept around so the undo action can put it back. */
    private var lastDeleted: Task? = null

    val uiState: StateFlow<TaskUiState> =
        combine(repository.tasks, filter) { tasks, activeFilter ->
            TaskUiState(
                tasks = tasks.filter { task ->
                    when (activeFilter) {
                        TaskFilter.ALL -> true
                        TaskFilter.ACTIVE -> !task.isDone
                        TaskFilter.COMPLETED -> task.isDone
                    }
                },
                filter = activeFilter,
                activeCount = tasks.count { !it.isDone },
                completedCount = tasks.count { it.isDone },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskUiState(),
        )

    fun setFilter(newFilter: TaskFilter) {
        filter.value = newFilter
    }

    fun add(title: String) {
        viewModelScope.launch { repository.add(title) }
    }

    fun toggle(task: Task) {
        viewModelScope.launch { repository.toggle(task) }
    }

    fun delete(task: Task) {
        lastDeleted = task
        viewModelScope.launch { repository.delete(task) }
    }

    fun undoDelete() {
        val task = lastDeleted ?: return
        lastDeleted = null
        viewModelScope.launch { repository.restore(task) }
    }

    fun clearCompleted() {
        viewModelScope.launch { repository.clearCompleted() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication
                TaskViewModel(application.container.taskRepository)
            }
        }
    }
}
