package com.example.demo.controller

import com.example.demo.dto.CreateTaskRequest
import com.example.demo.dto.TaskResponse
import com.example.demo.dto.UpdateTaskStatusRequest
import com.example.demo.model.TaskStatus
import com.example.demo.service.TaskService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val service: TaskService) {

    @PostMapping
    fun createTask(@Valid @RequestBody request: CreateTaskRequest): Mono<TaskResponse> =
        service.createTask(request)

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): Mono<TaskResponse> =
        service.getTaskById(id)

    @GetMapping
    fun getTasks(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ): Flux<TaskResponse> =
        service.getTasks(page, size, status)

    @PatchMapping("/{id}/status")
    fun updateTaskStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateTaskStatusRequest
    ): Mono<TaskResponse> =
        service.updateStatus(id, request)

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: Long): Mono<Void> =
        service.deleteTask(id)
}