package com.example.libreria_pde

class User(
    val ID: String,
    val userName: String,
    val passwordHash: String // Solo almacenamos el hash
) {
    constructor() : this("", "", "")
}
