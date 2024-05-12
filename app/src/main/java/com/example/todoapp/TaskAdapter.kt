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
    private var editTaskClickListener: EditTaskClickListener? = null
    private var deleteTaskClickListener: DeleteTaskClickListener? = null
    private var itemClickListener: ItemClickListener? = null

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

    fun setEditTaskClickListener(listener: EditTaskClickListener) {
        this.editTaskClickListener = listener
    }

    fun setDeleteTaskClickListener(listener: DeleteTaskClickListener) {
        this.deleteTaskClickListener = listener
    }

    fun setItemClickListener(listener: ItemClickListener) {
        this.itemClickListener = listener
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
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

            val priorityText = when (task.priority) {
                0 -> "Low"
                1 -> "Medium"
                2 -> "High"
                else -> "Unknown"
            }

            binding.priorityStatus.setBackgroundColor(priorityColor)
            binding.tvPriorityText.text = priorityText
            binding.tvPriorityText.setTextColor(priorityColor)

            binding.btnEdit.setOnClickListener {
                editTaskClickListener?.onEditTaskClick(task)
            }

            binding.btnDelete.setOnClickListener {
                deleteTaskClickListener?.onDeleteTaskClick(task)
            }

            itemView.setOnClickListener {
                itemClickListener?.onItemClick(task)
            }
        }
    }

    private fun formatDate(timestamp: Long?): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return timestamp?.let { dateFormat.format(Date(it)) } ?: ""
    }

    interface EditTaskClickListener {
        fun onEditTaskClick(task: Task)
    }

    interface DeleteTaskClickListener {
        fun onDeleteTaskClick(task: Task)
    }

    interface ItemClickListener {
        fun onItemClick(task: Task)
    }
}