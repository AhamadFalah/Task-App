package com.example.todoapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.todoapp.databinding.ActivityEditTaskBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class EditTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var categorySpinner: Spinner
    private var selectedDeadline: Long? = null
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        categorySpinner = binding.spinnerCategory
        setupCategorySpinner()

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

    private fun setupCategorySpinner() {
        val categories = listOf("Personal", "Work", "Shopping", "Others")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

    private fun loadTask(taskId: Int) {
        taskViewModel.getTaskById(taskId).observe(this) { task ->
            binding.etTaskTitle.setText(task.title)
            binding.etTaskDescription.setText(task.description)
            binding.spinnerPriority.setSelection(task.priority)
            binding.spinnerCategory.setSelection(getCategoryIndex(task.category))
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
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
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
        val priority = binding.spinnerPriority.selectedItemPosition
        val category = binding.spinnerCategory.selectedItem.toString()

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
                finish()
            }
        }
    }

    private fun getCategoryIndex(category: String?): Int {
        val categories = listOf("Personal", "Work", "Shopping", "Others")
        return categories.indexOf(category)
    }
}