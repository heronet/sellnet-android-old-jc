package com.heronet.sellnetbeta.web

data class ApiResponse<T>(
    val data: T,
    val size: Int
)
