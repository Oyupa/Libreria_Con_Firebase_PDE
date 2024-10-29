package com.example.libreria_pde

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class LoginActivity : ComponentActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseFirestore.getInstance()

        // Usa el diseño XML en lugar de Jetpack Compose
        setContentView(R.layout.login_activity)

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        // Configura el listener de "Iniciar sesión"
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            loginUser(username, password)
        }

        // Configura el listener de "Registrarse"
        buttonRegister.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            registerUser(username, password)
        }
    }

    private fun registerUser(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            showToast("Por favor, completa todos los campos")
            return
        }

        getAllUsernames { usernames ->
            if (usernames.contains(username)) {
                showToast("El nombre de usuario ya está en uso")
                return@getAllUsernames
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
                onUsernamesFetched(usernames)
            }
            .addOnFailureListener { e ->
                showToast("Error al obtener nombres de usuario: ${e.message}")
            }
    }
}
