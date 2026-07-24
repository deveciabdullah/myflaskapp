package com.deveciabdullah.todo

import android.app.Application
import com.deveciabdullah.todo.data.TaskRepository
import com.deveciabdullah.todo.data.TodoDatabase

/** Hand-rolled dependency container — small enough that a DI framework would be overkill. */
class AppContainer(application: Application) {
    private val database by lazy { TodoDatabase.build(application) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
}

class TodoApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
