package com.example.demo.service

import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import com.example.demo.repository.TaskRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TaskService(private val repository: TaskRepository) {

    fun create(task: Task): Task =
        repository.save(task)

    fun getById(id: Long): Task? =
        repository.findById(id)

    fun getAll(page: Int, size: Int, status: TaskStatus?): List<Task> =
        repository.findAll(page, size, status)

    fun count(status: TaskStatus?): Long =
        repository.count(status)

    fun updateStatus(id: Long, status: TaskStatus): Int =
        repository.updateStatus(id, status, LocalDateTime.now())

    fun deleteById(id: Long): Int =
        repository.deleteById(id)
}