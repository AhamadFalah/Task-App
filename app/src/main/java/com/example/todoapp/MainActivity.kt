package com.example.todoapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchTasks(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchTasks(newText ?: "")
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter_by_category -> {
                showFilterDialog()
                return true
            }
            R.id.action_sort_by_priority -> {
                displayTasksSortedByPriority()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFilterDialog() {
        // Implement the filter dialog to filter tasks by category
    }

    private fun searchTasks(query: String) {
        taskViewModel.searchTasks(query).observe(this) { tasks ->
            taskAdapter.updateTasks(tasks)
        }
    }

    private fun displayTasksSortedByPriority() {
        taskViewModel.getTasksSortedByPriority().observe(this) { tasks ->
            taskAdapter.updateTasks(tasks)
        }
    }
}