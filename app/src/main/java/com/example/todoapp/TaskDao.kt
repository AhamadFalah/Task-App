package com.example.todoapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert
    fun insert(task: Task): Long

    @Update
    fun update(task: Task): Int

    @Delete
    fun delete(task: Task): Int

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Int): Flow<Task>

    @Query("SELECT * FROM tasks WHERE category = :category")
    fun getTasksByCategory(category: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE title LIKE :query OR description LIKE :query")
    fun searchTasks(query: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    fun getTasksSortedByPriority(): Flow<List<Task>>
}