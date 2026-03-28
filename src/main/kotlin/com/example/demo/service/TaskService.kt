package com.example.demo.service

import com.example.demo.dto.TaskRequest
import com.example.demo.dto.TaskResponse
import com.example.demo.mapper.toResponse
import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import com.example.demo.repository.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class TaskService(private val taskRepository: TaskRepository) {

    fun createTask(request: TaskRequest): Mono<TaskResponse> {
        val task = Task(
            id = null,
            title = request.title,
            description = request.description,
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return taskRepository.save(task)
            .flatMap { id -> taskRepository.findById(id) }
            .map { it.toResponse() }
    }

    fun getTaskById(id: Long): Mono<TaskResponse> =
        taskRepository.findById(id).map { it.toResponse() }

    fun getTasks(page: Int, size: Int, status: TaskStatus?): Flux<TaskResponse> =
        taskRepository.findAll(page, size, status).map { it.toResponse() }

    fun updateStatus(id: Long, status: TaskStatus): Mono<TaskResponse> {
        val updatedAt = LocalDateTime.now()
        return taskRepository.updateStatus(id, status, updatedAt)
            .flatMap { taskRepository.findById(id) }
            .map { it.toResponse() }
    }

    fun deleteTask(id: Long): Mono<Void> =
        taskRepository.deleteById(id).then()
}