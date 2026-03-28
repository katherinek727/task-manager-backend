package com.example.demo.controller

import com.example.demo.dto.TaskRequest
import com.example.demo.dto.TaskResponse
import com.example.demo.mapper.TaskMapper
import com.example.demo.model.TaskStatus
import com.example.demo.service.TaskService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    fun createTask(@RequestBody request: TaskRequest): TaskResponse {
        val task = taskService.create(request)
        return TaskMapper.toResponse(task)
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): TaskResponse? {
        return taskService.getById(id)?.let { TaskMapper.toResponse(it) }
    }

    @GetMapping
    fun getTasks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ): List<TaskResponse> {
        return taskService.getAll(page, size, status)
            .map { TaskMapper.toResponse(it) }
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: Long): Int =
        taskService.deleteById(id)
}