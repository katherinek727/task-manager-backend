package com.example.demo.service

import com.example.demo.dto.TaskRequest
import com.example.demo.mapper.TaskMapper
import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import com.example.demo.repository.TaskRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TaskService(private val repository: TaskRepository) {

    fun create(request: TaskRequest): Task {
        val task = TaskMapper.toEntity(request)
        return repository.save(task)
    }

    fun getById(id: Long): Task? =
        repository.findById(id)

    fun getAll(page: Int, size: Int, status: TaskStatus?): List<Task> =
        repository.findAll(page, size, status)

    fun updateStatus(id: Long, status: TaskStatus): Int =
        repository.updateStatus(id, status, LocalDateTime.now())

    fun deleteById(id: Long): Int =
        repository.deleteById(id)
}