package com.countup.sqliteexample

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.countup.sqliteexample.adapters.TaskAdapter
import com.countup.sqliteexample.database.DatabaseHandler
import com.countup.sqliteexample.databinding.ActivityMainBinding
import com.countup.sqliteexample.databinding.DialogAddTaskBinding
import com.countup.sqliteexample.entity.EPriority
import com.countup.sqliteexample.entity.Task
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val dbHelper = DatabaseHandler(this)
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        updateTaskList()

        binding.floatingActionButton.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun updateTaskList() {
        val tasks = dbHelper.getAllTasks()
        displayTasks(tasks)
    }

    private fun displayTasks(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            binding.textEmpty.visibility = View.VISIBLE
            binding.recyclerTasks.visibility = View.GONE
        } else {
            binding.textEmpty.visibility = View.GONE
            binding.recyclerTasks.visibility = View.VISIBLE
            taskAdapter.updateTasks(tasks)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(emptyList())
        binding.recyclerTasks.adapter = taskAdapter
    }

    private fun showAddTaskDialog() {
        val dialogBinding = DialogAddTaskBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)

        val priorities = arrayOf("Alta", "Mais ou menos", "Foda se")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorities)
        dialogBinding.dropdownPriority.setAdapter(adapter)

        val dialog = builder.create()

        dialogBinding.editDueDate.setOnClickListener {
            showDatePickerDialog(dialogBinding.editDueDate)
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSave.setOnClickListener {
            saveTask(dialogBinding,dialog)
        }

        dialog.show()
    }

    private fun saveTask(dialogBinding: DialogAddTaskBinding, dialog: AlertDialog) {
        val title = dialogBinding.editTaskTitle.text.toString()
        val description = dialogBinding.editTaskDescription.text.toString()
        val dueDateString = dialogBinding.editDueDate.text.toString()
        val priorityString = dialogBinding.dropdownPriority.text.toString()

        if (title.isEmpty()) {
           dialogBinding.editTaskTitle.error = "Titulo e necessario"
            return
        }

        if (dueDateString.isEmpty()) {
            dialogBinding.editDueDate.error = "Data e necessaria"
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dueDate = try {
            dateFormat.parse(dueDateString) ?: Date()
        } catch (e: Exception) {
            Date()
        }

        val priority = when (priorityString) {
            "Alta" -> EPriority.HIGH
            "Mais ou Menos" -> EPriority.MEDIUM
            "Foda se" -> EPriority.LOW
            else -> EPriority.MEDIUM
        }

        val task = Task(
            title,
            description,
            timeLimit = dueDate,
            priority
        )

        val id = dbHelper.addTask(task)

        if (id > 0) {
            Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
            updateTaskList()
            dialog.dismiss()
        } else  {
            Toast.makeText(this,"Deu ruim", Toast.LENGTH_LONG)
        }

    }

    private fun showDatePickerDialog(editDueDate: TextInputEditText) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            {_, year, month, dayOfMonth -> calendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                editDueDate.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}