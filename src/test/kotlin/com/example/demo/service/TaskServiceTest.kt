package com.example.demo.service

import com.example.demo.dto.CreateTaskRequest
import com.example.demo.dto.UpdateTaskStatusRequest
import com.example.demo.exception.TaskNotFoundException
import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import com.example.demo.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

class TaskServiceTest {

    private lateinit var repository: TaskRepository
    private lateinit var service: TaskService

    @BeforeEach
    fun setup() {
        repository = mockk()
        service = TaskService(repository)
    }

    @Test
    fun `createTask should return created task`() {
        val request = CreateTaskRequest("Test Task", "Description")
        val now = LocalDateTime.now()
        val task = Task(1L, request.title, request.description, TaskStatus.NEW, now, now)

        every { repository.save(any()) } returns Mono.just(task)

        StepVerifier.create(service.createTask(request))
            .expectNextMatches { it.id == 1L && it.title == "Test Task" }
            .verifyComplete()
    }

    @Test
    fun `getTaskById should return task when exists`() {
        val task = Task(1L, "Test", "Desc", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now())
        every { repository.findById(1L) } returns Mono.just(task)

        StepVerifier.create(service.getTaskById(1L))
            .expectNextMatches { it.id == 1L && it.title == "Test" }
            .verifyComplete()
    }

    @Test
    fun `getTaskById should return error when task missing`() {
        every { repository.findById(1L) } returns Mono.empty()

        StepVerifier.create(service.getTaskById(1L))
            .expectError(TaskNotFoundException::class.java)
            .verify()
    }

    @Test
    fun `updateStatus should update and return task`() {
        val task = Task(1L, "Test", "Desc", TaskStatus.DONE, LocalDateTime.now(), LocalDateTime.now())
        every { repository.updateStatus(1L, TaskStatus.DONE, any()) } returns Mono.just(1)
        every { repository.findById(1L) } returns Mono.just(task)

        StepVerifier.create(service.updateStatus(1L, UpdateTaskStatusRequest(TaskStatus.DONE)))
            .expectNextMatches { it.status == TaskStatus.DONE }
            .verifyComplete()
    }

    @Test
    fun `deleteTask should complete when task exists`() {
        every { repository.deleteById(1L) } returns Mono.just(1)

        StepVerifier.create(service.deleteTask(1L))
            .verifyComplete()
    }

    @Test
    fun `getTasks should return list of tasks`() {
        val tasks = listOf(
            Task(1L, "Test1", "Desc1", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now()),
            Task(2L, "Test2", "Desc2", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now())
        )
        every { repository.findAll(0, 10, TaskStatus.NEW) } returns Flux.fromIterable(tasks)

        StepVerifier.create(service.getTasks(0, 10, TaskStatus.NEW))
            .expectNextCount(2)
            .verifyComplete()
    }
}