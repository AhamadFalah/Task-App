package com.example.todoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private var tasks: List<Task>,
    private val context: Context
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.tvTaskTitle.text = task.title
            binding.tvTaskDescription.text = task.description
            binding.tvTaskCategory.text = task.category
            binding.tvTaskDeadline.text = formatDate(task.deadline)

            val priorityColor = when (task.priority) {
                0 -> ContextCompat.getColor(context, R.color.priority_low)
                1 -> ContextCompat.getColor(context, R.color.priority_medium)
                2 -> ContextCompat.getColor(context, R.color.priority_high)
                else -> ContextCompat.getColor(context, R.color.priority_default)
            }
            binding.priorityIndicator.setBackgroundColor(priorityColor)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    private fun formatDate(timestamp: Long?): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return timestamp?.let { dateFormat.format(Date(it)) } ?: ""
    }
}