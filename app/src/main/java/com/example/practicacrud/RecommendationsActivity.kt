// Crear en app/src/main/java/com/example/practicacrud/RecommendationsActivity.kt
package com.example.practicacrud

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practicacrud.adapters.RecommendationsAdapter
import com.example.practicacrud.repository.DataRepository
import com.example.practicacrud.services.RecommendationService
import com.example.practicacrud.utils.AuthManager
import kotlinx.coroutines.launch

class RecommendationsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView

    private lateinit var authManager: AuthManager
    private lateinit var repository: DataRepository
    private lateinit var recommendationService: RecommendationService
    private lateinit var adapter: RecommendationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommendations)

        authManager = AuthManager(this)
        repository = DataRepository(this)
        recommendationService = RecommendationService(this)

        // Verificar que el usuario esté autenticado
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "Debe iniciar sesión para ver recomendaciones", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupRecyclerView()
        loadRecommendations()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyView = findViewById(R.id.emptyView)
    }

    private fun setupRecyclerView() {
        adapter = RecommendationsAdapter(
            onItemClick = { recommendation ->
                Toast.makeText(this, "Seleccionado: ${recommendation.title}", Toast.LENGTH_SHORT).show()
            },
            onAddToFavorites = { recommendation ->
                addToFavorites(recommendation)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadRecommendations() {
        showLoading()

        lifecycleScope.launch {
            try {
                val userId = authManager.getUserId()
                val recommendations = recommendationService.getRecommendations(userId)

                if (recommendations.isEmpty()) {
                    showEmpty("No hay recomendaciones disponibles.\nAgrega favoritos o realiza búsquedas para obtener recomendaciones personalizadas.")
                } else {
                    showRecommendations(recommendations)
                }
            } catch (e: Exception) {
                Toast.makeText(this@RecommendationsActivity, "Error al cargar recomendaciones: ${e.message}", Toast.LENGTH_LONG).show()
                showEmpty("Error al cargar recomendaciones")
            }
        }
    }

    private fun addToFavorites(recommendation: RecommendationService.Recommendation) {
        lifecycleScope.launch {
            try {
                val userId = authManager.getUserId()

                // Verificar si ya es favorito
                if (repository.isFavorite(userId, recommendation.id, recommendation.type)) {
                    Toast.makeText(this@RecommendationsActivity, "Ya está en favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    repository.addToFavorites(
                        userId = userId,
                        itemId = recommendation.id,
                        itemType = recommendation.type,
                        title = recommendation.title,
                        subtitle = recommendation.subtitle,
                        imageUrl = recommendation.imageUrl
                    )
                    Toast.makeText(this@RecommendationsActivity, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RecommendationsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.GONE
    }

    private fun showRecommendations(recommendations: List<RecommendationService.Recommendation>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        adapter.setRecommendations(recommendations)
    }

    private fun showEmpty(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
        emptyView.text = message
    }
}