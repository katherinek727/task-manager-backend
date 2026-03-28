package com.example.demo.exception

import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(e: TaskNotFoundException): Mono<Map<String, String>> =
        Mono.just(mapOf("error" to (e.message ?: "Not found")))

    @ExceptionHandler(WebExchangeBindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(e: WebExchangeBindException): Mono<Map<String, Any>> {
        val errors = e.bindingResult.allErrors.associate { error ->
            val field = if (error is FieldError) error.field else "error"
            field to (error.defaultMessage ?: "Invalid value")
        }
        return Mono.just(mapOf("errors" to errors))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgument(e: IllegalArgumentException): Mono<Map<String, String>> =
        Mono.just(mapOf("error" to (e.message ?: "Bad request")))
}
