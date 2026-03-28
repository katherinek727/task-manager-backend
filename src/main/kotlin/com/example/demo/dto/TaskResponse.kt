package com.example.demo.dto

import java.time.LocalDateTime

data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
