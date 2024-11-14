package com.example.libreria_pde

data class Novel(
    val title: String,
    val author: String = "Anónimo",
    val year: Int = 0,
    val synopsis: String = "",
    var isFavorite: Boolean = false,
    val reviews: MutableList<String> = mutableListOf()

) {
    constructor() : this("", "Anónimo", 0, "", false, mutableListOf())

    fun Novel.toSerializable(): NovelSerializable {
        return NovelSerializable(
            title = this.title,
            author = this.author,
            year = this.year,
            synopsis = this.synopsis,
            isFavorite = this.isFavorite,
            reviews = this.reviews
        )
    }

    fun toSerializable(): NovelSerializable {
        return NovelSerializable(
            title = this.title,
            author = this.author,
            year = this.year,
            synopsis = this.synopsis,
            isFavorite = this.isFavorite,
            reviews = this.reviews
        )
    }

}

