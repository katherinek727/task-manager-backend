package com.example.demo.repository

import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
class TaskRepositories(private val client: DatabaseClient) {

    fun save(task: Task): Mono<Long> {
        return client.sql(
            """
            INSERT INTO tasks(title, description, status, created_at, updated_at)
            VALUES (:title, :description, :status, :createdAt, :updatedAt)
            RETURNING id
            """
        )
            .bind("title", task.title)
            .bind("description", task.description ?: "")
            .bind("status", task.status.name)
            .bind("createdAt", task.createdAt)
            .bind("updatedAt", task.updatedAt)
            .map { row -> row["id"] as Long }
            .one()
    }

    fun findById(id: Long): Mono<Task> {
        return client.sql("SELECT * FROM tasks WHERE id = :id")
            .bind("id", id)
            .map { row, _ ->
                Task(
                    id = row["id"] as Long,
                    title = row["title"] as String,
                    description = row["description"] as? String,
                    status = TaskStatus.valueOf(row["status"] as String),
                    createdAt = row["created_at"] as LocalDateTime,
                    updatedAt = row["updated_at"] as LocalDateTime
                )
            }
            .one()
    }

    fun findAll(page: Int, size: Int, status: TaskStatus?): Flux<Task> {
        val sql = StringBuilder("SELECT * FROM tasks")
        if (status != null) sql.append(" WHERE status = :status")
        sql.append(" ORDER BY created_at DESC LIMIT :limit OFFSET :offset")

        val query = client.sql(sql.toString())
            .bind("limit", size)
            .bind("offset", page * size)

        if (status != null) query.bind("status", status.name)

        return query.map { row, _ ->
            Task(
                id = row["id"] as Long,
                title = row["title"] as String,
                description = row["description"] as? String,
                status = TaskStatus.valueOf(row["status"] as String),
                createdAt = row["created_at"] as LocalDateTime,
                updatedAt = row["updated_at"] as LocalDateTime
            )
        }.all()
    }

    fun updateStatus(id: Long, status: TaskStatus, updatedAt: LocalDateTime): Mono<Int> {
        return client.sql("UPDATE tasks SET status = :status, updated_at = :updatedAt WHERE id = :id")
            .bind("status", status.name)
            .bind("updatedAt", updatedAt)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .map { it.toInt() }
    }

    fun deleteById(id: Long): Mono<Int> {
        return client.sql("DELETE FROM tasks WHERE id = :id")
            .bind("id", id)
            .fetch()
            .rowsUpdated()
            .map { it.toInt() }
    }
}