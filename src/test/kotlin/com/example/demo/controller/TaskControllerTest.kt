package com.example.demo.controller

import com.example.demo.dto.CreateTaskRequest
import com.example.demo.dto.PageResponse
import com.example.demo.dto.TaskResponse
import com.example.demo.dto.UpdateTaskStatusRequest
import com.example.demo.exception.GlobalExceptionHandler
import com.example.demo.exception.TaskNotFoundException
import com.example.demo.model.TaskStatus
import com.example.demo.service.TaskService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

class TaskControllerTest {

    private lateinit var service: TaskService
    private lateinit var client: WebTestClient

    private val now = LocalDateTime.now()

    @BeforeEach
    fun setup() {
        service = mockk()
        client = WebTestClient
            .bindToController(TaskController(service))
            .controllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `POST createTask returns 200 with task`() {
        val request = CreateTaskRequest("Test Task", "Desc")
        val response = TaskResponse(1L, "Test Task", "Desc", "NEW", now, now)

        every { service.createTask(request) } returns Mono.just(response)

        client.post().uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.title").isEqualTo("Test Task")
            .jsonPath("$.status").isEqualTo("NEW")
    }

    @Test
    fun `POST createTask returns 400 when title is blank`() {
        val request = CreateTaskRequest("", "Desc")

        client.post().uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `POST createTask returns 400 when title is too short`() {
        val request = CreateTaskRequest("ab", "Desc")

        client.post().uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `GET getTaskById returns 200 with task`() {
        val response = TaskResponse(1L, "Test Task", "Desc", "NEW", now, now)
        every { service.getTaskById(1L) } returns Mono.just(response)

        client.get().uri("/api/tasks/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
    }

    @Test
    fun `GET getTaskById returns 404 when task not found`() {
        every { service.getTaskById(99L) } returns Mono.error(TaskNotFoundException("Task 99 not found"))

        client.get().uri("/api/tasks/99")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `GET getTasks returns paginated response`() {
        val pageResponse = PageResponse(
            content = listOf(
                TaskResponse(1L, "Test1", "Desc1", "NEW", now, now),
                TaskResponse(2L, "Test2", "Desc2", "NEW", now, now)
            ),
            page = 0, size = 10, totalElements = 2L, totalPages = 1
        )
        every { service.getTasks(0, 10, TaskStatus.NEW) } returns Mono.just(pageResponse)

        client.get().uri("/api/tasks?page=0&size=10&status=NEW")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.content.length()").isEqualTo(2)
            .jsonPath("$.totalElements").isEqualTo(2)
            .jsonPath("$.totalPages").isEqualTo(1)
            .jsonPath("$.page").isEqualTo(0)
            .jsonPath("$.size").isEqualTo(10)
    }

    @Test
    fun `PATCH updateTaskStatus returns 200 with updated task`() {
        val request = UpdateTaskStatusRequest(TaskStatus.DONE)
        val response = TaskResponse(1L, "Test Task", "Desc", "DONE", now, now)

        every { service.updateStatus(1L, request) } returns Mono.just(response)

        client.patch().uri("/api/tasks/1/status")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("DONE")
    }

    @Test
    fun `PATCH updateTaskStatus returns 404 when task not found`() {
        val request = UpdateTaskStatusRequest(TaskStatus.DONE)
        every { service.updateStatus(99L, request) } returns Mono.error(TaskNotFoundException("Task 99 not found"))

        client.patch().uri("/api/tasks/99/status")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `DELETE deleteTask returns 204`() {
        every { service.deleteTask(1L) } returns Mono.empty()

        client.delete().uri("/api/tasks/1")
            .exchange()
            .expectStatus().isNoContent
    }
}
