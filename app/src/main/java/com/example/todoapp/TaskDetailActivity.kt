package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        val priorityColor = when (task.priority) {
            0 -> ContextCompat.getColor(this, R.color.priority_low)
            1 -> ContextCompat.getColor(this, R.color.priority_medium)
            2 -> ContextCompat.getColor(this, R.color.priority_high)
            else -> ContextCompat.getColor(this, R.color.priority_default)
        }

        val priorityText = when (task.priority) {
            0 -> "Low"
            1 -> "Medium"
            2 -> "High"
            else -> "Unknown"
        }

        binding.tvTaskPriority.text = priorityText
        binding.tvTaskPriority.setTextColor(priorityColor)

        binding.tvTaskCategory.text = task.category
        binding.tvTaskDeadline.text = formatDate(task.deadline)
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