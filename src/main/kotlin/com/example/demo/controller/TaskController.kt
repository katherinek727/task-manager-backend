package com.example.demo.controller

import com.example.demo.dto.CreateTaskRequest
import com.example.demo.dto.PageResponse
import com.example.demo.dto.TaskResponse
import com.example.demo.dto.UpdateTaskStatusRequest
import com.example.demo.model.TaskStatus
import com.example.demo.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    fun createTask(@Valid @RequestBody request: CreateTaskRequest): Mono<TaskResponse> =
        taskService.createTask(request)

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): Mono<TaskResponse> =
        taskService.getTaskById(id)

    @GetMapping
    fun getTasks(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ): Mono<PageResponse<TaskResponse>> = taskService.getTasks(page, size, status)

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateTaskStatusRequest
    ): Mono<TaskResponse> = taskService.updateStatus(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(@PathVariable id: Long): Mono<Void> =
        taskService.deleteTask(id)
}
