package com.example.libreria_pde

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class NovelDetailFragment : Fragment() {

    private lateinit var novel: NovelSerializable

    companion object {
        private const val ARG_NOVEL = "arg_novel"

        // Metodo para instanciar el fragmento con la novela seleccionada
        fun newInstance(novel: NovelSerializable): NovelDetailFragment {
            val fragment = NovelDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_NOVEL, novel)  // Cambiado a putSerializable
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            novel = it.getSerializable(ARG_NOVEL) as NovelSerializable // Cambiado a getSerializable
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_novel_detail, container, false)

        // Configuración de los textos
        view.findViewById<TextView>(R.id.textViewNovelSynopsis).text =
            if (novel.synopsis.isEmpty()) "No hay sinopsis" else "Sinopsis: ${novel.synopsis}"
        view.findViewById<TextView>(R.id.textViewReviews).text = "Reseñas:\n${novel.reviews.joinToString("\n")}"

        // Configuración del botón de favorito
        val favoriteButton = view.findViewById<Button>(R.id.buttonFavorite)
        favoriteButton.text = if (novel.isFavorite) "Desmarcar Favorita" else "Marcar como Favorita"
        favoriteButton.setOnClickListener {
            novel.isFavorite = !novel.isFavorite
            favoriteButton.text = if (novel.isFavorite) "Desmarcar Favorita" else "Marcar como Favorita"
            // Aquí podrías actualizar el adaptador o la base de datos si es necesario
        }

        // Configuración del botón para cerrar el fragmento
        view.findViewById<Button>(R.id.buttonCloseDetails).setOnClickListener {
            parentFragmentManager.popBackStack()  // Cierra el fragmento
        }

        return view
    }
}
