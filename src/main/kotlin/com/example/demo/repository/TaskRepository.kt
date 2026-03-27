package com.example.demo.repository

import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
class TaskRepository(private val client: DatabaseClient) {

    fun save(task: Task): Mono<Task> {
        val sql = """
            INSERT INTO tasks (title, description, status, created_at, updated_at)
            VALUES (:title, :description, :status, :createdAt, :updatedAt)
            RETURNING id
        """
        return client.sql(sql)
            .bind("title", task.title)
            .bind("description", task.description)
            .bind("status", task.status.name)
            .bind("createdAt", task.createdAt)
            .bind("updatedAt", task.updatedAt)
            .map { row -> task.copy(id = row.get("id", java.lang.Long::class.java)?.toLong()) }
            .one()
    }

    fun findById(id: Long): Mono<Task> {
        val sql = "SELECT * FROM tasks WHERE id = :id"
        return client.sql(sql)
            .bind("id", id)
            .map { row ->
                Task(
                    id = row.get("id", java.lang.Long::class.java)?.toLong(),
                    title = row.get("title", String::class.java)!!,
                    description = row.get("description", String::class.java),
                    status = TaskStatus.valueOf(row.get("status", String::class.java)!!),
                    createdAt = row.get("created_at", LocalDateTime::class.java)!!,
                    updatedAt = row.get("updated_at", LocalDateTime::class.java)!!
                )
            }
            .one()
    }

    fun findAll(page: Int, size: Int, status: TaskStatus?): Flux<Task> {
        val offset = page * size
        val baseSql = StringBuilder("SELECT * FROM tasks")
        status?.let { baseSql.append(" WHERE status = '$status'") }
        baseSql.append(" ORDER BY created_at DESC LIMIT $size OFFSET $offset")
        return client.sql(baseSql.toString())
            .map { row ->
                Task(
                    id = row.get("id", java.lang.Long::class.java)?.toLong(),
                    title = row.get("title", String::class.java)!!,
                    description = row.get("description", String::class.java),
                    status = TaskStatus.valueOf(row.get("status", String::class.java)!!),
                    createdAt = row.get("created_at", LocalDateTime::class.java)!!,
                    updatedAt = row.get("updated_at", LocalDateTime::class.java)!!
                )
            }
            .all()
    }

    fun updateStatus(id: Long, status: TaskStatus, updatedAt: LocalDateTime): Mono<Int> {
        val sql = "UPDATE tasks SET status = :status, updated_at = :updatedAt WHERE id = :id"
        return client.sql(sql)
            .bind("status", status.name)
            .bind("updatedAt", updatedAt)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
    }

    fun deleteById(id: Long): Mono<Int> {
        val sql = "DELETE FROM tasks WHERE id = :id"
        return client.sql(sql)
            .bind("id", id)
            .fetch()
            .rowsUpdated()
    }
}