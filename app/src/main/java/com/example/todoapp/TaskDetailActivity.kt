package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todoapp.databinding.ActivityTaskDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val task = intent.getParcelableExtra<Task>("task")
        if (task != null) {
            displayTaskDetails(task)
        }

        binding.btnEditTask.setOnClickListener {
            val intent = Intent(this, EditTaskActivity::class.java)
            intent.putExtra("task_id", task?.id)
            startActivity(intent)
        }

        binding.btnDeleteTask.setOnClickListener {
            task?.let { t ->
                TaskViewModel(application).delete(t)
                finish()
            }
        }
    }

    private fun displayTaskDetails(task: Task) {
        binding.tvTaskTitle.text = task.title
        binding.tvTaskDescription.text = task.description
        binding.tvTaskPriority.text = "Priority: ${getPriorityString(task.priority)}"
        binding.tvTaskCategory.text = "Category: ${task.category}"
        binding.tvTaskDeadline.text = "Deadline: ${formatDate(task.deadline)}"
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