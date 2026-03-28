package com.example.demo.service

import com.example.demo.dto.CreateTaskRequest
import com.example.demo.dto.PageResponse
import com.example.demo.dto.TaskResponse
import com.example.demo.dto.UpdateTaskStatusRequest
import com.example.demo.exception.TaskNotFoundException
import com.example.demo.mapper.toResponse
import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import com.example.demo.repository.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import kotlin.math.ceil

@Service
class TaskService(private val taskRepository: TaskRepository) {

    fun createTask(request: CreateTaskRequest): Mono<TaskResponse> {
        val task = Task(
            id = null,
            title = request.title,
            description = request.description,
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return taskRepository.save(task).map { it.toResponse() }
    }

    fun getTaskById(id: Long): Mono<TaskResponse> =
        taskRepository.findById(id)
            .switchIfEmpty(Mono.error(TaskNotFoundException("Task $id not found")))
            .map { it.toResponse() }

    fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<PageResponse<TaskResponse>> {
        val tasksMono = taskRepository.findAll(page, size, status).map { it.toResponse() }.collectList()
        val countMono = taskRepository.count(status)
        return Mono.zip(tasksMono, countMono).map { tuple ->
            val tasks = tuple.t1
            val total = tuple.t2
            PageResponse(
                content = tasks,
                page = page,
                size = size,
                totalElements = total,
                totalPages = ceil(total.toDouble() / size).toInt()
            )
        }
    }

    fun updateStatus(id: Long, request: UpdateTaskStatusRequest): Mono<TaskResponse> {
        val updatedAt = LocalDateTime.now()
        return taskRepository.updateStatus(id, request.status, updatedAt)
            .flatMap {
                if (it == 0) Mono.error(TaskNotFoundException("Task $id not found"))
                else taskRepository.findById(id)
            }
            .switchIfEmpty(Mono.error(TaskNotFoundException("Task $id not found")))
            .map { it.toResponse() }
    }

    fun deleteTask(id: Long): Mono<Void> =
        taskRepository.deleteById(id).then()
}
