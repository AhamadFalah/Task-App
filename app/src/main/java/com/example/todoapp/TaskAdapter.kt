package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitleTextView: TextView = itemView.findViewById(R.id.tv_task_title)
        val taskDescriptionTextView: TextView = itemView.findViewById(R.id.tv_task_description)
        val taskDeadlineTextView: TextView = itemView.findViewById(R.id.tv_task_deadline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTitleTextView.text = task.title
        holder.taskDescriptionTextView.text = task.description
        holder.taskDeadlineTextView.text = task.deadline?.let { formatDeadline(it) } ?: "No deadline"
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    private fun formatDeadline(deadline: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date(deadline))
    }
}