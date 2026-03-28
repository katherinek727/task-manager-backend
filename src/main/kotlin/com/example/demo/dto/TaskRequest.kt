package com.example.demo.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class TaskRequest(
    @field:NotBlank(message = "Title must not be blank")
    @field:Size(min = 3, max = 100, message = "Title length must be between 3 and 100")
    val title: String,
    val description: String? = null
)