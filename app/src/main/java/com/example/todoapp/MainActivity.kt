package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        taskAdapter = TaskAdapter(emptyList(), this)

        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }

        taskViewModel.allTasks.observe(this) { tasks ->
            taskAdapter.updateTasks(tasks)
        }

        binding.fabAddTask.setOnClickListener {
            val intent = Intent(this, TaskAddActivity::class.java)
            startActivity(intent)
        }

        setUpClickListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    taskViewModel.searchTasks(it).observe(this@MainActivity, { tasks ->
                        taskAdapter.updateTasks(tasks)
                    })
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter_by_category -> {
                showFilterByCategoryDialog()
                true
            }
            R.id.action_sort_by_priority -> {
                taskViewModel.getTasksSortedByPriority().observe(this, { tasks ->
                    taskAdapter.updateTasks(tasks)
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpClickListeners() {
        val editTaskClickListener = object : TaskAdapter.EditTaskClickListener {
            override fun onEditTaskClick(task: Task) {
                val intent = Intent(this@MainActivity, EditTaskActivity::class.java)
                intent.putExtra("task_id", task.id)
                startActivity(intent)
            }
        }

        val deleteTaskClickListener = object : TaskAdapter.DeleteTaskClickListener {
            override fun onDeleteTaskClick(task: Task) {
                taskViewModel.delete(task)
            }
        }

        val itemClickListener = object : TaskAdapter.ItemClickListener {
            override fun onItemClick(task: Task) {
                val intent = Intent(this@MainActivity, TaskDetailActivity::class.java)
                intent.putExtra("task", task)
                startActivity(intent)
            }
        }

        taskAdapter.setEditTaskClickListener(editTaskClickListener)
        taskAdapter.setDeleteTaskClickListener(deleteTaskClickListener)
        taskAdapter.setItemClickListener(itemClickListener)
    }

    private fun showFilterByCategoryDialog() {
        val categories = listOf("Personal", "Work", "Shopping", "Others")
        val selectedCategory = MutableLiveData<String?>()

        val dialogView = layoutInflater.inflate(R.layout.dialog_filter_by_category, null)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spinner_category)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Filter") { _, _ ->
                val selectedCategoryIndex = categorySpinner.selectedItemPosition
                val selectedCategoryName = categories[selectedCategoryIndex]
                selectedCategory.value = selectedCategoryName
                taskViewModel.getTasksByCategory(selectedCategoryName).observe(this, { tasks ->
                    taskAdapter.updateTasks(tasks)
                })
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}