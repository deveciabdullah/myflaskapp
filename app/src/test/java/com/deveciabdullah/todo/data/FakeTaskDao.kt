package com.deveciabdullah.todo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory [TaskDao] that mirrors the ordering guarantees of the Room query. */
class FakeTaskDao : TaskDao {

    private val rows = MutableStateFlow<List<Task>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<Task>> = rows.map { list ->
        list.sortedWith(compareBy({ it.isDone }, { -it.createdAt }))
    }

    override suspend fun insert(task: Task): Long {
        val id = nextId++
        rows.value = rows.value + task.copy(id = id)
        return id
    }

    override suspend fun update(task: Task) {
        rows.value = rows.value.map { if (it.id == task.id) task else it }
    }

    override suspend fun delete(task: Task) {
        rows.value = rows.value.filterNot { it.id == task.id }
    }

    override suspend fun deleteCompleted() {
        rows.value = rows.value.filterNot { it.isDone }
    }
}
