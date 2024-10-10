package com.example.libreria_pde

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var novelAdapter: NovelAdapter
    private var novelList: MutableList<Novel> = mutableListOf()
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Cargar las novelas desde SharedPreferences
        loadNovelsFromDatabase()

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.recyclerViewNovels)
        novelAdapter = NovelAdapter(novelList, { novel -> showNovelDetails(novel) }, { novel -> deleteNovel(novel) })
        recyclerView.adapter = novelAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Botón para agregar una nueva novela
        val addNovelButton: Button = findViewById(R.id.buttonAddNovel)
        addNovelButton.setOnClickListener {
            addNewNovel()
        }

    }

    // Método para agregar una nueva novela
    private fun addNewNovel() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Agregar Nueva Novela")

        // Crear un layout para el formulario
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 20)

        val titleInput = EditText(this)
        titleInput.hint = "Título de la novela"
        layout.addView(titleInput)

        val authorInput = EditText(this)
        authorInput.hint = "Autor de la novela (opcional)"
        layout.addView(authorInput)

        val yearInput = EditText(this)
        yearInput.hint = "Año de publicación (opcional, solo números)"
        layout.addView(yearInput)

        val synopsisInput = EditText(this)
        synopsisInput.hint = "Sinopsis breve (opcional)"
        layout.addView(synopsisInput)

        dialogBuilder.setView(layout)

        dialogBuilder.setPositiveButton("Agregar") { _, _ ->
            val title = titleInput.text.toString().trim()
            val author = if (authorInput.text.toString().trim().isEmpty()) "Anónimo" else authorInput.text.toString().trim()
            val yearString = yearInput.text.toString().trim()
            val synopsis = synopsisInput.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            // Validar el año
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val year = if (yearString.isEmpty()) {
                0.toString() // Guardar como cadena vacía si no se proporciona
            } else {
                try {
                    // Intentar convertir el año a un número entero
                    val yearInt = yearString.toInt()
                    if (yearInt > currentYear) {
                        Toast.makeText(this, "El año no puede ser mayor que el año actual.", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    yearInt.toString() // Convertir de nuevo a cadena para guardar
                } catch (e: NumberFormatException) {
                    // Capturar el error si no se puede convertir a entero
                    Toast.makeText(this, "El año debe ser un número válido.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
            }

            val newNovel = Novel(title, author, Integer.parseInt(year), synopsis)
            novelList.add(newNovel)
            novelAdapter.notifyDataSetChanged()
            saveNovelsToDatabase() // Guardar las novelas después de agregar
        }
        dialogBuilder.setNegativeButton("Cancelar", null)

        // Mostrar el diálogo
        dialogBuilder.show()
    }


    // Método para eliminar una novela
    private fun deleteNovel(novel: Novel) {
        novelList.remove(novel)
        novelAdapter.notifyDataSetChanged()
        db.collection("Libreria").document(novel.title).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Novela eliminada con éxito", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar la novela: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        saveNovelsToDatabase()// Guardar las novelas después de eliminar
    }

    // Método para mostrar los detalles de la novela seleccionada
    private fun showNovelDetails(novel: Novel) {
        // Mostrar el layout de detalles
        val novelDetailView: LinearLayout = findViewById(R.id.novelDetailView)
        if (novelDetailView.visibility == View.GONE) {
            val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
            novelDetailView.startAnimation(slideUp)
            novelDetailView.visibility = View.VISIBLE
        }

        // Asignar la información de la novela a los TextView correspondientes
        findViewById<TextView>(R.id.textViewNovelSynopsis).text = if (novel.synopsis=="")"No hay sinopsis" else "Sinopsis: ${novel.synopsis}"
        findViewById<TextView>(R.id.textViewReviews).text ="Reseñas:" + novel.reviews.joinToString("\n")

        // Botón para marcar como favorita
        val favoriteButton = findViewById<Button>(R.id.buttonFavorite)
        favoriteButton.text = if (novel.isFavorite) "Desmarcar Favorita" else "Marcar como Favorita"
        favoriteButton.setOnClickListener {
            novel.isFavorite = !novel.isFavorite
            // Cambiar el texto del botón según el estado
            favoriteButton.text = if (novel.isFavorite) "Desmarcar Favorita" else "Marcar como Favorita"
            novelAdapter.notifyDataSetChanged()
            saveNovelsToDatabase()// Guardar las novelas después de cambiar el estado de favorita
        }

        // Botón para agregar reseñas
        findViewById<Button>(R.id.buttonAddReview).setOnClickListener {
            addReview(novel)
        }

        // Botón para cerrar los detalles
        findViewById<Button>(R.id.buttonCloseDetails).setOnClickListener {
            val slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)
            novelDetailView.startAnimation(slideDown)
            novelDetailView.visibility = View.GONE
        }
    }


    // Método para agregar reseñas a la novela seleccionada
    private fun addReview(novel: Novel) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Agregar Reseña")

        // Crear campo para la reseña
        val reviewInput = EditText(this)
        reviewInput.hint = "Escribe tu reseña aquí"
        dialogBuilder.setView(reviewInput)

        // Botones del diálogo
        dialogBuilder.setPositiveButton("Agregar") { _, _ ->
            val review = reviewInput.text.toString().trim()
            if (review.isEmpty()) {
                Toast.makeText(this, "La reseña no puede estar vacía", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            novel.reviews.add(review)
            findViewById<TextView>(R.id.textViewReviews).text = novel.reviews.joinToString("\n")
            saveNovelsToDatabase() // Guardar las novelas después de agregar reseña
        }
        dialogBuilder.setNegativeButton("Cancelar", null)

        // Mostrar el diálogo
        dialogBuilder.show()
    }

    private fun saveNovelsToDatabase() {
        var exito = true
        for (novel in novelList) {
            db.collection("Libreria").document(novel.title).set(novel)
                /*.addOnSuccessListener {
                    Toast.makeText(this, "Novela guardada con éxito", Toast.LENGTH_SHORT).show()
                }*/
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error al guardar la novela: ${exception.message}", Toast.LENGTH_SHORT).show()
                    exito = false
                }
        }
        if (exito) Toast.makeText(this, "Novelas guardadas con éxito", Toast.LENGTH_SHORT).show()
    }

    private fun loadNovelsFromDatabase() {
        db.collection("Libreria")
            .get()
            .addOnSuccessListener { result ->
                novelList.clear()
                for (document in result) {
                    val novel = document.toObject(Novel::class.java)
                    novelList.add(novel)
                }
                novelAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar las novelas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
