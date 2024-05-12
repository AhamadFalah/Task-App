package com.example.todoapp

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskAddActivity : AppCompatActivity() {
    private lateinit var taskTitleEditText: EditText
    private lateinit var taskDescriptionEditText: EditText
    private lateinit var prioritySpinner: Spinner
    private lateinit var deadlineEditText: EditText
    private lateinit var saveButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)

        deadlineEditText = findViewById(R.id.deadline_edittext)
        deadlineEditText.setOnClickListener {
            showDatePicker()
        }

        taskTitleEditText = findViewById(R.id.task_title)
        taskDescriptionEditText = findViewById(R.id.task_description)
        prioritySpinner = findViewById(R.id.priority_spinner)
        deadlineEditText = findViewById(R.id.deadline_edittext)
        saveButton = findViewById(R.id.save_button)


        saveButton.setOnClickListener {
            saveTask()
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
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                deadlineEditText.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun saveTask() {
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

            val newTask = Task(
                title = taskTitle,
                description = taskDescription,
                priority = taskPriority,
                deadline = taskDeadline
            )



            }
    }


}