package com.example.demo.dto

import com.example.demo.model.TaskStatus

data class UpdateTaskStatusRequest(
    val status: TaskStatus
)