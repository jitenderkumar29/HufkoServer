package com.hufko.dto

data class ImportResult(
    val totalProcessed: Int,
    val successful: Int,
    val failed: Int,
    val errors: List<String> = emptyList()
)