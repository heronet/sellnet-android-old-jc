package com.heronet.sellnetbeta.web

data class RegisterDto(
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val city: String,
    val division: String
)
