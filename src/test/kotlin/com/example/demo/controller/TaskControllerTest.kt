package com.example.demo.controller

import com.example.demo.dto.CreateTaskRequest
import com.example.demo.dto.TaskResponse
import com.example.demo.dto.UpdateTaskStatusRequest
import com.example.demo.model.TaskStatus
import com.example.demo.service.TaskService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

class TaskControllerTest {

    private lateinit var service: TaskService
    private lateinit var client: WebTestClient

    @BeforeEach
    fun setup() {
        service = mockk()
        client = WebTestClient.bindToController(TaskController(service)).build()
    }

    @Test
    fun `POST createTask returns 200 with task`() {
        val request = CreateTaskRequest("Test Task", "Desc")
        val response = TaskResponse(1L, "Test Task", "Desc", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now())

        every { service.createTask(request) } returns Mono.just(response)

        client.post().uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.title").isEqualTo("Test Task")
    }

    @Test
    fun `GET getTaskById returns 200 with task`() {
        val response = TaskResponse(1L, "Test Task", "Desc", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now())
        every { service.getTaskById(1L) } returns Mono.just(response)

        client.get().uri("/api/tasks/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
    }

    @Test
    fun `GET getTasks returns list of tasks`() {
        val tasks = listOf(
            TaskResponse(1L, "Test1", "Desc1", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now()),
            TaskResponse(2L, "Test2", "Desc2", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now())
        )
        every { service.getTasks(0, 10, TaskStatus.NEW) } returns Flux.fromIterable(tasks)

        client.get().uri("/api/tasks?page=0&size=10&status=NEW")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(TaskResponse::class.java)
            .hasSize(2)
    }

    @Test
    fun `PATCH updateTaskStatus returns updated task`() {
        val request = UpdateTaskStatusRequest(TaskStatus.DONE)
        val response = TaskResponse(1L, "Test Task", "Desc", TaskStatus.DONE, LocalDateTime.now(), LocalDateTime.now())

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
    fun `DELETE deleteTask returns 204`() {
        every { service.deleteTask(1L) } returns Mono.empty()

        client.delete().uri("/api/tasks/1")
            .exchange()
            .expectStatus().isNoContent
    }
}