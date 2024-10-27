package com.example.libreria_pde

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class LoginActivity : ComponentActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()

        setContent {
            LoginScreen()
        }
    }

    @Composable
    fun LoginScreen() {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = { loginUser(username, password) }) {
                    Text("Login")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { registerUser(username, password) }) {
                    Text("Register")
                }
            }
        }
    }

    private fun registerUser(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            showToast("Por favor, completa todos los campos")
            return
        }

        // Obtiene todos los usernames y verifica si el username ya existe
        getAllUsernames { usernames ->
            if (usernames.contains(username)) {
                showToast("El nombre de usuario ya está en uso")
                return@getAllUsernames // Termina la función si el nombre ya existe
            }

            val hashedPassword = hashPassword(password)
            val userId = db.collection("Users").document().id

            val user = User(userId, username, hashedPassword)

            db.collection("Users")
                .document(userId)
                .set(user)
                .addOnSuccessListener {
                    showToast("Usuario registrado con éxito")
                }
                .addOnFailureListener { e ->
                    showToast("Error al registrar usuario: ${e.message}")
                }
        }
    }

    private fun loginUser(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            showToast("Por favor, completa todos los campos")
            return
        }

        val hashedPassword = hashPassword(password)

        db.collection("Users")
            .whereEqualTo("userName", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val storedHash = document.getString("passwordHash")
                        if (storedHash == hashedPassword) {
                            showToast("Inicio de sesión exitoso")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            return@addOnSuccessListener
                        }
                    }
                    showToast("Usuario o contraseña incorrectos")
                } else {
                    showToast("Usuario o contraseña incorrectos")
                }
            }
            .addOnFailureListener { e ->
                showToast("Error al iniciar sesión: ${e.message}")
            }
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { String.format("%02x", it) }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun getAllUsernames(onUsernamesFetched: (List<String>) -> Unit) {
        db.collection("Users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val usernames = mutableListOf<String>()
                for (document in querySnapshot.documents) {
                    val username = document.getString("userName")
                    if (username != null) {
                        usernames.add(username)
                    }
                }
                onUsernamesFetched(usernames) // Retorna la lista de usernames
            }
            .addOnFailureListener { e ->
                showToast("Error al obtener nombres de usuario: ${e.message}")
            }
    }

}
