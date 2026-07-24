package com.deveciabdullah.todo.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskRepositoryTest {

    private fun repository() = TaskRepository(FakeTaskDao())

    @Test
    fun `add trims the title`() = runTest {
        val repository = repository()

        assertTrue(repository.add("  ekmek al  "))

        assertEquals(listOf("ekmek al"), repository.tasks.first().map { it.title })
    }

    @Test
    fun `add rejects blank titles`() = runTest {
        val repository = repository()

        assertFalse(repository.add("   "))

        assertTrue(repository.tasks.first().isEmpty())
    }

    @Test
    fun `toggle flips the done flag`() = runTest {
        val repository = repository()
        repository.add("ekmek al")
        val task = repository.tasks.first().single()

        repository.toggle(task)
        assertTrue(repository.tasks.first().single().isDone)

        repository.toggle(repository.tasks.first().single())
        assertFalse(repository.tasks.first().single().isDone)
    }

    @Test
    fun `restore brings a deleted task back with its original title`() = runTest {
        val repository = repository()
        repository.add("ekmek al")
        val task = repository.tasks.first().single()

        repository.delete(task)
        assertTrue(repository.tasks.first().isEmpty())

        repository.restore(task)
        assertEquals(listOf("ekmek al"), repository.tasks.first().map { it.title })
    }

    @Test
    fun `clearCompleted removes only finished tasks`() = runTest {
        val repository = repository()
        repository.add("biten")
        repository.add("bekleyen")
        val done = repository.tasks.first().single { it.title == "biten" }
        repository.toggle(done)

        repository.clearCompleted()

        assertEquals(listOf("bekleyen"), repository.tasks.first().map { it.title })
    }
}
