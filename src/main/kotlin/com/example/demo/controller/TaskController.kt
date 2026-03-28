package com.example.demo.controller

import com.example.demo.dto.TaskRequest
import com.example.demo.dto.TaskResponse
import com.example.demo.model.TaskStatus
import com.example.demo.service.TaskService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    fun createTask(@Valid @RequestBody request: TaskRequest): Mono<TaskResponse> =
        taskService.createTask(request)

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): Mono<TaskResponse> =
        taskService.getTaskById(id)

    @GetMapping
    fun getTasks(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ): Flux<TaskResponse> = taskService.getTasks(page, size, status)

    @PatchMapping("/{id}/status")
    fun updateStatus(@PathVariable id: Long, @RequestBody statusRequest: Map<String,String>): Mono<TaskResponse> {
        val status = TaskStatus.valueOf(statusRequest["status"]!!)
        return taskService.updateStatus(id, status)
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: Long): Mono<Void> =
        taskService.deleteTask(id)
}