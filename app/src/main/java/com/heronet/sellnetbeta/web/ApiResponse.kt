package com.heronet.sellnetbeta.web

data class ApiResponse<T>(
    var data: T,
    val size: Int
)
