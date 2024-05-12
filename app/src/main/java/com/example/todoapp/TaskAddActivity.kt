package com.example.todoapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todoapp.databinding.ActivityTaskAddBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TaskAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskAddBinding
    private lateinit var categorySpinner: Spinner
    private var selectedDeadline: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categorySpinner = binding.spinnerCategory
        setupCategorySpinner()

        binding.etTaskDeadline.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnSaveTask.setOnClickListener {
            saveTask()
        }
    }

    private fun setupCategorySpinner() {
        val categories = listOf("Personal", "Work", "Shopping", "Others")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
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

    private fun saveTask() {
        val title = binding.etTaskTitle.text.toString().trim()
        val description = binding.etTaskDescription.text.toString().trim()
        val priority = binding.spinnerPriority.selectedItemPosition
        val category = binding.spinnerCategory.selectedItem.toString()

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
}