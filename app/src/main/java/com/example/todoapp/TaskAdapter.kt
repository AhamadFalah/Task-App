package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private var tasks: List<Task>,
    private val onItemClick: (Task) -> Unit,
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitleTextView: TextView = itemView.findViewById(R.id.tv_task_title)
        val taskDescriptionTextView: TextView = itemView.findViewById(R.id.tv_task_description)
        val taskDeadlineTextView: TextView = itemView.findViewById(R.id.tv_task_deadline)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val task = tasks[position]
                    onItemClick(task)
                }
            }

            itemView.findViewById<ImageView>(R.id.iv_edit_task).setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val task = tasks[position]
                    onEditClick(task)
                }
            }

            itemView.findViewById<ImageView>(R.id.iv_delete_task).setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val task = tasks[position]
                    onDeleteClick(task)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTitleTextView.text = task.title
        holder.taskDescriptionTextView.text = task.description
        holder.taskDeadlineTextView.text = formatDeadline(task.deadline)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    private fun formatDeadline(deadline: Long?): String {
        return if (deadline != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.format(Date(deadline))
        } else {
            "No deadline"
        }
    }

    fun updateTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }
}