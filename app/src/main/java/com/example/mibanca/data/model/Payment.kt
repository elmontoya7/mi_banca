package com.example.mibanca.data.model

import java.util.*

data class Payment(
    val date: Date,
    val location: String,
    val fromCard: String,
    val toCard: String,
    val toName: String,
    val subject: String
)
