package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        taskAdapter = TaskAdapter(
            onItemClick = { task ->
                val intent = Intent(this, TaskDetailActivity::class.java)
                intent.putExtra("task", task)
                startActivity(intent)
            },
            onEditClick = { task ->
                val intent = Intent(this, EditTaskActivity::class.java)
                intent.putExtra("task", task)
                startActivity(intent)
            },
            onDeleteClick = { task ->
                taskViewModel.deleteTask(task)
            }
        )

        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = taskAdapter

        retrieveTasksFromDatabase()

        binding.buttonAdd.setOnClickListener {
            val intent = Intent(this, TaskAddActivity::class.java)
            startActivity(intent)
        }
    }

    private fun retrieveTasksFromDatabase() {
        taskViewModel.allTasks.observe(this) { tasks ->
            taskAdapter.updateTasks(tasks)
        }
    }
}