package com.deveciabdullah.todo.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {

    val tasks: Flow<List<Task>> = dao.observeAll()

    /** Adds a task, ignoring blank input. Returns true when something was stored. */
    suspend fun add(title: String): Boolean {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return false
        dao.insert(Task(title = trimmed))
        return true
    }

    suspend fun toggle(task: Task) = dao.update(task.copy(isDone = !task.isDone))

    suspend fun delete(task: Task) = dao.delete(task)

    /**
     * Puts a deleted task back. The row keeps its original [Task.createdAt] so it
     * lands where the user last saw it, but Room assigns a fresh id.
     */
    suspend fun restore(task: Task) {
        dao.insert(task.copy(id = 0))
    }

    suspend fun clearCompleted() = dao.deleteCompleted()
}
