package com.heronet.sellnetbeta.model

data class AuthData(
    val id: String,
    val name: String,
    val roles: List<String>,
    val token: String
)
