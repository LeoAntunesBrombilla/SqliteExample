package com.countup.sqliteexample.entity

import java.util.Date

enum class EPriority(val displayName: String) {
   HIGH("Alta"),
   MEDIUM("Media") ,
   LOW("Foda se")
}

data class Task(
    val title: String,
    val description: String,
    val timeLimit: Date,
    val priority: EPriority
)