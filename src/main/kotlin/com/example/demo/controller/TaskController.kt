package com.example.demo.controller

import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import com.example.demo.service.TaskService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    fun createTask(@RequestBody task: Task): Task =
        taskService.create(task)

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): Task? =
        taskService.getById(id)

    @GetMapping
    fun getTasks(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ): List<Task> =
        taskService.getAll(page, size, status)

    @PutMapping("/{id}/status")
    fun updateTaskStatus(
        @PathVariable id: Long,
        @RequestParam status: TaskStatus
    ): Int =
        taskService.updateStatus(id, status)

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: Long): Int =
        taskService.deleteById(id)
}