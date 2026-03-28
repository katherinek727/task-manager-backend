package com.example.demo.dto

import javax.validation.constraints.NotBlank

data class UpdateTaskStatusRequest(
    @field:NotBlank(message = "Status must not be blank")
    val status: String
)