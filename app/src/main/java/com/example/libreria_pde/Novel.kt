package com.example.libreria_pde

data class Novel(
    val title: String,
    val author: String = "Anónimo",
    val year: String = "",
    val synopsis: String = "",
    var isFavorite: Boolean = false,
    val reviews: MutableList<String> = mutableListOf()
)
