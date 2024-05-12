package com.example.todoapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskDetailActivity : AppCompatActivity() {
    private lateinit var taskTitleTextView: TextView
    private lateinit var taskDescriptionTextView: TextView
    private lateinit var taskPriorityTextView: TextView
    private lateinit var taskCategoryTextView: TextView
    private lateinit var taskDeadlineTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        taskTitleTextView = findViewById(R.id.tv_task_title)
        taskDescriptionTextView = findViewById(R.id.tv_task_description)
        taskPriorityTextView = findViewById(R.id.tv_task_priority)
        taskCategoryTextView = findViewById(R.id.tv_task_category)
        taskDeadlineTextView = findViewById(R.id.tv_task_deadline)

        val task = intent.getParcelableExtra<Task>("task")
        if (task != null) {
            displayTaskDetails(task)
        }
    }

    private fun displayTaskDetails(task: Task) {
        taskTitleTextView.text = task.title
        taskDescriptionTextView.text = task.description
        taskPriorityTextView.text = "Priority: ${getPriorityString(task.priority)}"
        taskCategoryTextView.text = "Category: ${task.category}"
        taskDeadlineTextView.text = "Deadline: ${formatDate(task.deadline)}"
    }

    private fun getPriorityString(priority: Int): String {
        return when (priority) {
            0 -> "Low"
            1 -> "Medium"
            2 -> "High"
            else -> "Unknown"
        }
    }

    private fun formatDate(timestamp: Long?): String {
        return if (timestamp != null) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        } else {
            "No deadline"
        }
    }
}