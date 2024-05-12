package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskDatabase: TaskDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var taskAdapter: TaskAdapter

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database and DAO
        taskDatabase = TaskDatabase.getInstance(applicationContext)
        taskDao = taskDatabase.taskDao()

        // Set up the RecyclerView
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(emptyList())
        binding.rvTasks.adapter = taskAdapter

        // Set up the FAB click listener
        binding.buttonAdd.setOnClickListener {
            val intent = Intent(this, TaskAddActivity::class.java)
            startActivity(intent)
        }

        // Retrieve and display tasks from the database
        retrieveTasksFromDatabase()
    }

    override fun onDestroy() {
        coroutineContext.cancel()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        retrieveTasksFromDatabase()
    }

    private fun retrieveTasksFromDatabase() {
        launch {
            taskDao.getAllTasks().collect { tasks ->
                taskAdapter.updateTasks(tasks)
            }
        }
    }

    inner class TaskAdapter(private var tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
        inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val taskTitleTextView: TextView = itemView.findViewById(R.id.tv_task_title)
            val taskDescriptionTextView: TextView = itemView.findViewById(R.id.tv_task_description)
            val taskDeadlineTextView: TextView = itemView.findViewById(R.id.tv_task_deadline)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
            return TaskViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val task = tasks[position]
            holder.taskTitleTextView.text = task.title
            holder.taskDescriptionTextView.text = task.description
            holder.taskDeadlineTextView.text = task.deadline?.let { formatDeadline(it) } ?: "No deadline"
        }

        override fun getItemCount(): Int {
            return tasks.size
        }

        private fun formatDeadline(deadline: Long): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(Date(deadline))
        }

        fun updateTasks(tasks: List<Task>) {
            this.tasks = tasks
            notifyDataSetChanged()
        }
    }
}