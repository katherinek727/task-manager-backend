package com.example.demo.mapper

import com.example.demo.dto.TaskResponse
import com.example.demo.model.Task

fun Task.toResponse() = TaskResponse(
    id = this.id!!,
    title = this.title,
    description = this.description,
    status = this.status.name,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
