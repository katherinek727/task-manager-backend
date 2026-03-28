package com.example.demo.mapper

import com.example.demo.model.Task
import com.example.demo.dto.TaskResponse

// Extension function: Task -> TaskResponse
fun Task.toResponse(): TaskResponse {
    return TaskResponse(
        id = this.id,
        title = this.title,
        description = this.description,
        status = this.status.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}