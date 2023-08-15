package com.example.mibanca.data.model

import java.util.*

data class User(
    val user: String,
    val password: ByteArray,
    val cards: MutableList<Card>,
    val payments: MutableList<Payment>
)
