package com.example.demo.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException::class)
    fun handleTaskNotFound(ex: TaskNotFoundException): ResponseEntity<Map<String, String>> {
        val body = mapOf("error" to ex.message.orEmpty())
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationError(ex: WebExchangeBindException): ResponseEntity<Map<String, String>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "") }
        return ResponseEntity(mapOf("errors" to errors.toString()), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralError(ex: Exception): ResponseEntity<Map<String, String>> {
        return ResponseEntity(mapOf("error" to (ex.message ?: "Internal server error")), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
