package com.example.todoapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todoapp.databinding.ActivityTaskAddBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TaskAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskAddBinding
    private lateinit var actvCategory: AutoCompleteTextView
    private lateinit var actvPriority: AutoCompleteTextView
    private var selectedDeadline: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actvCategory = binding.actvCategory
        actvPriority = binding.actvPriority
        setupCategoryDropdown()
        setupPriorityDropdown()

        binding.etTaskDeadline.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnSaveTask.setOnClickListener {
            saveTask()
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

    private fun saveTask() {
        val title = binding.etTaskTitle.text.toString().trim()
        val description = binding.etTaskDescription.text.toString().trim()
        val priority = getPriorityInt(actvPriority.text.toString())
        val category = actvCategory.text.toString()

        if (title.isNotEmpty()) {
            val task = Task(
                title = title,
                description = description,
                priority = priority,
                deadline = selectedDeadline,
                category = category
            )
            lifecycleScope.launch(Dispatchers.IO) {
                TaskDatabase.getInstance(applicationContext).taskDao().insert(task)
                finish()
            }
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