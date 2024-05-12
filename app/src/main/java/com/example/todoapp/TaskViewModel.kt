package com.example.todoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = TaskDatabase.getInstance(application).taskDao()

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks().asLiveData()

    fun insert(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.insert(task)
        }
    }

    fun update(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.update(task)
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.delete(task)
        }
    }

    fun getTasksByCategory(category: String): LiveData<List<Task>> {
        return taskDao.getTasksByCategory(category).asLiveData()
    }

    fun searchTasks(query: String): LiveData<List<Task>> {
        return taskDao.searchTasks("%$query%").asLiveData()
    }

    fun getTasksSortedByPriority(): LiveData<List<Task>> {
        return taskDao.getTasksSortedByPriority().asLiveData()
    }

    fun getTaskById(taskId: Int): LiveData<Task> {
        return taskDao.getTaskById(taskId).asLiveData()
    }
}