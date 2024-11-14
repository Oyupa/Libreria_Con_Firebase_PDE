package com.example.libreria_pde

import java.io.Serializable

data class NovelSerializable(
    val title: String,
    val author: String,
    val year: Int,
    val synopsis: String,
    var isFavorite: Boolean,
    val reviews: List<String>
) : Serializable
