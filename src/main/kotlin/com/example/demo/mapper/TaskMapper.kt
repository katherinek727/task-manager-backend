package com.example.demo.mapper

import com.example.demo.dto.TaskRequest
import com.example.demo.dto.TaskResponse
import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import java.time.LocalDateTime

object TaskMapper {

    fun toEntity(request: TaskRequest): Task {
        val now = LocalDateTime.now()

        return Task(
            id = null,
            title = request.title,
            description = request.description,
            status = TaskStatus.TODO,
            createdAt = now,
            updatedAt = now
        )
    }

    fun toResponse(task: Task): TaskResponse {
        return TaskResponse(
            id = task.id!!,
            title = task.title,
            description = task.description,
            status = task.status,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
    }
}