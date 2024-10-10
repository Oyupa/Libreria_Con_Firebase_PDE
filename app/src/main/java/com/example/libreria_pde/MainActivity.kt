package com.example.libreria_pde

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var novelAdapter: NovelAdapter
    private lateinit var novelList: MutableList<Novel> // Lista de novelas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Cargar las novelas desde SharedPreferences
        loadNovelsFromPreferences()

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
                ""
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
            saveNovelsToPreferences() // Guardar las novelas después de agregar
        }
        dialogBuilder.setNegativeButton("Cancelar", null)

        // Mostrar el diálogo
        dialogBuilder.show()
    }


    // Método para eliminar una novela
    private fun deleteNovel(novel: Novel) {
        novelList.remove(novel)
        novelAdapter.notifyDataSetChanged()
        saveNovelsToPreferences() // Guardar las novelas después de eliminar
    }

    // Método para mostrar los detalles de la novela seleccionada
    // Método para mostrar los detalles de la novela seleccionada
    private fun showNovelDetails(novel: Novel) {
        // Mostrar el layout de detalles
        val novelDetailView: LinearLayout = findViewById(R.id.novelDetailView)
        novelDetailView.visibility = View.VISIBLE

        // Asignar la información de la novela a los TextView correspondientes
        //findViewById<TextView>(R.id.textViewNovelTitle).text = novel.title
        //findViewById<TextView>(R.id.textViewNovelAuthor).text = "Autor: ${novel.author}"
        //findViewById<TextView>(R.id.textViewNovelYear).text = "Año: ${novel.year}"
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
            saveNovelsToPreferences() // Guardar las novelas después de cambiar el estado de favorita
        }

        // Botón para agregar reseñas
        findViewById<Button>(R.id.buttonAddReview).setOnClickListener {
            addReview(novel)
        }

        // Botón para cerrar los detalles
        findViewById<Button>(R.id.buttonCloseDetails).setOnClickListener {
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
            saveNovelsToPreferences() // Guardar las novelas después de agregar reseña
        }
        dialogBuilder.setNegativeButton("Cancelar", null)

        // Mostrar el diálogo
        dialogBuilder.show()
    }

    private fun saveNovelsToPreferences() {
        val sharedPreferences = getSharedPreferences("NovelLibraryPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(novelList)
        editor.putString("novelList", json)
        editor.apply()
    }

    private fun loadNovelsFromPreferences() {
        val sharedPreferences = getSharedPreferences("NovelLibraryPrefs", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("novelList", null)
        val type = object : TypeToken<MutableList<Novel>>() {}.type
        novelList = if (json != null) {
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}
