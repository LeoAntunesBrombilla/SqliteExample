package com.countup.sqliteexample.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.countup.sqliteexample.entity.EPriority
import com.countup.sqliteexample.entity.Task
import java.util.Date

class DatabaseHandler (context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(banco: SQLiteDatabase?) {
        banco?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "time_limit INTEGER, " +
                "priority TEXT NOT NULL" +
                ")")
    }

    override fun onUpgrade(banco: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        banco?.execSQL( "DROP TABLE IF EXISTS ${TABLE_NAME}" )
        onCreate( banco )
    }

    fun addTask(task: Task): Long {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put("title", task.title)
            put("description", task.description)
            put("time_limit", task.timeLimit.time)
            put("priority", task.priority.name)
        }

        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val priorityStr = cursor.getString(cursor.getColumnIndexOrThrow("priority"))
                val priority = try {
                    EPriority.valueOf(priorityStr)
                } catch (e: Exception) {
                    when (priorityStr) {
                        "Alta" -> EPriority.HIGH
                        "Media" -> EPriority.MEDIUM
                        "Foda se" -> EPriority.LOW
                        else -> EPriority.MEDIUM
                    }
                }

                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("time_limit"))
                val date = Date(timestamp)

                val task = Task(
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    timeLimit = date,
                    priority = priority
                )
                taskList.add(task)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return taskList
    }

    companion object {
        public const val DB_NAME = "task_db"
        public const val DB_VERSION = 1
        public const val TABLE_NAME = "task"
    }
}