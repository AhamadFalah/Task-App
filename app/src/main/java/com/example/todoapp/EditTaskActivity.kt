package com.example.todoapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {
    private lateinit var taskTitleEditText: EditText
    private lateinit var taskDescriptionEditText: EditText
    private lateinit var prioritySpinner: Spinner
    private lateinit var deadlineEditText: EditText
    private lateinit var saveButton: ImageView
    private lateinit var taskViewModel: TaskViewModel
    private var taskId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        taskTitleEditText = findViewById(R.id.edit_task_title)
        taskDescriptionEditText = findViewById(R.id.edit_task_description)
        prioritySpinner = findViewById(R.id.edit_priority_spinner)
        deadlineEditText = findViewById(R.id.edit_deadline_edittext)
        saveButton = findViewById(R.id.edit_save_button)

        val task = intent.getParcelableExtra<Task>("task")
        if (task != null) {
            taskId = task.id
            taskTitleEditText.setText(task.title)
            taskDescriptionEditText.setText(task.description)
            prioritySpinner.setSelection(task.priority)
            deadlineEditText.setText(task.deadline?.let { formatDate(it) })
        }

        deadlineEditText.setOnClickListener {
            showDatePicker()
        }

        saveButton.setOnClickListener {
            updateTask()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val formattedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                deadlineEditText.setText(formattedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun updateTask() {
        val taskTitle = taskTitleEditText.text.toString().trim()
        val taskDescription = taskDescriptionEditText.text.toString().trim()
        val taskPriority = prioritySpinner.selectedItemPosition
        val taskDeadlineString = deadlineEditText.text.toString().trim()

        if (taskTitle.isNotEmpty()) {
            val taskDeadline = if (taskDeadlineString.isNotEmpty()) {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(taskDeadlineString)?.time
            } else {
                null
            }

            val updatedTask = Task(
                id = taskId,
                title = taskTitle,
                description = taskDescription,
                priority = taskPriority,
                deadline = taskDeadline
            )

            taskViewModel.updateTask(updatedTask)
            finish()
        }
    }

    private fun formatDate(timestamp: Long?): String {
        return if (timestamp != null) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        } else {
            ""
        }
    }
}