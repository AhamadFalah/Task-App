package com.example.todoapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.todoapp.databinding.ActivityEditTaskBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class EditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var actvCategory: AutoCompleteTextView
    private lateinit var actvPriority: AutoCompleteTextView
    private var selectedDeadline: Long? = null
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        actvCategory = binding.actvCategory
        actvPriority = binding.actvPriority
        setupCategoryDropdown()
        setupPriorityDropdown()

        val taskId = intent.getIntExtra("task_id", -1)
        if (taskId != -1) {
            loadTask(taskId)
        }

        binding.etTaskDeadline.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnUpdateTask.setOnClickListener {
            updateTask(taskId)
        }
    }

    private fun setupCategoryDropdown() {
        val categories = listOf("Personal", "Work", "Shopping", "Others")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        actvCategory.setAdapter(adapter)
    }

    private fun setupPriorityDropdown() {
        val priorities = listOf("Low", "Medium", "High")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorities)
        actvPriority.setAdapter(adapter)
    }

    private fun loadTask(taskId: Int) {
        taskViewModel.getTaskById(taskId).observe(this) { task ->
            binding.etTaskTitle.setText(task.title)
            binding.etTaskDescription.setText(task.description)
            actvPriority.setText(getPriorityString(task.priority), false)
            actvCategory.setText(task.category, false)
            selectedDeadline = task.deadline
            binding.etTaskDeadline.setText(formatDate(selectedDeadline))
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: android.widget.DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                selectedDeadline = calendar.timeInMillis
                binding.etTaskDeadline.setText(formatDate(selectedDeadline))
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun formatDate(timestamp: Long?): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp ?: 0
        val dateFormat = android.text.format.DateFormat.getDateFormat(this)
        return dateFormat.format(calendar.time)
    }

    private fun updateTask(taskId: Int) {
        val title = binding.etTaskTitle.text.toString().trim()
        val description = binding.etTaskDescription.text.toString().trim()
        val priority = getPriorityInt(actvPriority.text.toString())
        val category = actvCategory.text.toString()

        if (title.isNotEmpty()) {
            val task = Task(
                id = taskId,
                title = title,
                description = description,
                priority = priority,
                deadline = selectedDeadline,
                category = category
            )
            lifecycleScope.launch(Dispatchers.IO) {
                TaskDatabase.getInstance(applicationContext).taskDao().update(task)
                finish() // Finish the activity and navigate back to MainActivity
            }
        }
    }

    private fun getPriorityString(priority: Int): String {
        return when (priority) {
            0 -> "Low"
            1 -> "Medium"
            2 -> "High"
            else -> ""
        }
    }

    private fun getPriorityInt(priority: String): Int {
        return when (priority) {
            "Low" -> 0
            "Medium" -> 1
            "High" -> 2
            else -> 0
        }
    }
}