package com.example.demo.service

import com.example.demo.dto.CreateTaskRequest
import com.example.demo.dto.TaskResponse
import com.example.demo.dto.UpdateTaskStatusRequest
import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import com.example.demo.repository.TaskRepository
import com.example.demo.exception.TaskNotFoundException
import com.example.demo.mapper.toResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class TaskService(private val repository: TaskRepository) {

    fun createTask(request: CreateTaskRequest): Mono<TaskResponse> {
        val now = LocalDateTime.now()
        val task = Task(
            id = null,
            title = request.title,
            description = request.description,
            status = TaskStatus.NEW,
            createdAt = now,
            updatedAt = now
        )
        return repository.save(task).map { it.toResponse() }
    }

    fun getTaskById(id: Long): Mono<TaskResponse> =
        repository.findById(id)
            .switchIfEmpty(Mono.error(TaskNotFoundException("Task with id $id not found")))
            .map { it.toResponse() }

    fun getTasks(page: Int, size: Int, status: TaskStatus?): Flux<TaskResponse> =
        repository.findAll(page, size, status)
            .map { it.toResponse() }

    fun updateStatus(id: Long, request: UpdateTaskStatusRequest): Mono<TaskResponse> {
        val now = LocalDateTime.now()
        return repository.updateStatus(id, request.status, now)
            .flatMap { rows ->
                if (rows == 0) Mono.error(TaskNotFoundException("Task with id $id not found"))
                else repository.findById(id).map { it.toResponse() }
            }
    }

    fun deleteTask(id: Long): Mono<Void> =
        repository.deleteById(id)
            .flatMap { rows ->
                if (rows == 0) Mono.error(TaskNotFoundException("Task with id $id not found"))
                else Mono.empty()
            }

    private fun Task.toResponse() =
        TaskResponse(id!!, title, description, status, createdAt, updatedAt)
}