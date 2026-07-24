package com.deveciabdullah.todo.ui

import com.deveciabdullah.todo.data.FakeTaskDao
import com.deveciabdullah.todo.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = TaskViewModel(TaskRepository(FakeTaskDao()))

    @Test
    fun `added tasks appear in the ui state`() = runTest(dispatcher) {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }

        viewModel.add("ekmek al")

        assertEquals(listOf("ekmek al"), viewModel.uiState.value.tasks.map { it.title })
        assertEquals(1, viewModel.uiState.value.activeCount)
        assertEquals(0, viewModel.uiState.value.completedCount)
    }

    @Test
    fun `blank input is ignored`() = runTest(dispatcher) {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }

        viewModel.add("   ")

        assertTrue(viewModel.uiState.value.tasks.isEmpty())
    }

    @Test
    fun `filters narrow the visible list without changing the counts`() = runTest(dispatcher) {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        viewModel.add("biten")
        viewModel.add("bekleyen")
        viewModel.toggle(viewModel.uiState.value.tasks.single { it.title == "biten" })

        viewModel.setFilter(TaskFilter.ACTIVE)
        assertEquals(listOf("bekleyen"), viewModel.uiState.value.tasks.map { it.title })

        viewModel.setFilter(TaskFilter.COMPLETED)
        assertEquals(listOf("biten"), viewModel.uiState.value.tasks.map { it.title })

        assertEquals(1, viewModel.uiState.value.activeCount)
        assertEquals(1, viewModel.uiState.value.completedCount)
    }

    @Test
    fun `undo restores the last deleted task`() = runTest(dispatcher) {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        viewModel.add("ekmek al")

        viewModel.delete(viewModel.uiState.value.tasks.single())
        assertTrue(viewModel.uiState.value.tasks.isEmpty())

        viewModel.undoDelete()
        assertEquals(listOf("ekmek al"), viewModel.uiState.value.tasks.map { it.title })
    }

    @Test
    fun `undo is a no-op when nothing was deleted`() = runTest(dispatcher) {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        viewModel.add("ekmek al")

        viewModel.undoDelete()

        assertEquals(1, viewModel.uiState.value.tasks.size)
    }

    @Test
    fun `clearCompleted keeps unfinished tasks`() = runTest(dispatcher) {
        val viewModel = viewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        viewModel.add("biten")
        viewModel.add("bekleyen")
        viewModel.toggle(viewModel.uiState.value.tasks.single { it.title == "biten" })

        viewModel.clearCompleted()

        assertEquals(listOf("bekleyen"), viewModel.uiState.value.tasks.map { it.title })
    }
}
