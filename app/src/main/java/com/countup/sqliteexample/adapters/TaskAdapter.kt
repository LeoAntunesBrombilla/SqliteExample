package com.countup.sqliteexample.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.countup.sqliteexample.databinding.ItemTaskBinding
import com.countup.sqliteexample.entity.EPriority
import com.countup.sqliteexample.entity.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(private var tasks: List<Task>): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount() = tasks.size

    //Pra q serve esse metodo?
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        with(holder.binding) {
            textTitle.text = task.title
            textDescription.text = task.description
            textDueDate.text = dateFormat.format(task.timeLimit)

            val colorRes = when (task.priority) {
                EPriority.HIGH -> android.R.color.holo_red_light
                EPriority.MEDIUM -> android.R.color.holo_orange_light
                EPriority.LOW -> android.R.color.holo_green_light
            }

            cardPriority.setCardBackgroundColor(root.context.getColor(colorRes))
            textPriority.text = when (task.priority) {
                EPriority.HIGH -> "Alta"
                EPriority.MEDIUM -> "Mais ou menos"
                EPriority.LOW -> "Foda se"
            }
        }
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}