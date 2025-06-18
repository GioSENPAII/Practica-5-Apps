// Crear en app/src/main/java/com/example/practicacrud/FavoritesActivity.kt
package com.example.practicacrud

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practicacrud.adapters.SearchResultsAdapter
import com.example.practicacrud.repository.DataRepository
import com.example.practicacrud.utils.AuthManager
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView

    private lateinit var authManager: AuthManager
    private lateinit var repository: DataRepository
    private lateinit var adapter: SearchResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        authManager = AuthManager(this)
        repository = DataRepository(this)

        // Verificar que el usuario esté autenticado
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "Debe iniciar sesión para ver favoritos", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupRecyclerView()
        loadFavorites()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        emptyView = findViewById(R.id.emptyView)
    }

    private fun setupRecyclerView() {
        adapter = SearchResultsAdapter(
            onItemClick = { item ->
                Toast.makeText(this, "Seleccionado: ${item.title}", Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { item ->
                removeFavorite(item)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            try {
                val userId = authManager.getUserId()
                val isAdmin = authManager.isAdmin()

                // Si es admin, puede ver todos los favoritos
                val favorites = if (isAdmin) {
                    repository.getAllFavorites()
                } else {
                    repository.getUserFavorites(userId)
                }

                if (favorites.isEmpty()) {
                    showEmpty()
                } else {
                    val searchItems = favorites.map { favorite ->
                        SearchResultsAdapter.SearchItem(
                            id = favorite.itemId,
                            title = favorite.title,
                            subtitle = favorite.subtitle ?: "",
                            imageUrl = favorite.imageUrl,
                            type = favorite.itemType,
                            isFavorite = true
                        )
                    }
                    showFavorites(searchItems)
                }
            } catch (e: Exception) {
                Toast.makeText(this@FavoritesActivity, "Error al cargar favoritos: ${e.message}", Toast.LENGTH_LONG).show()
                showEmpty()
            }
        }
    }

    private fun removeFavorite(item: SearchResultsAdapter.SearchItem) {
        lifecycleScope.launch {
            try {
                val userId = authManager.getUserId()
                repository.removeFromFavorites(userId, item.id, item.type)
                Toast.makeText(this@FavoritesActivity, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()

                // Recargar la lista
                loadFavorites()
            } catch (e: Exception) {
                Toast.makeText(this@FavoritesActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showFavorites(items: List<SearchResultsAdapter.SearchItem>) {
        recyclerView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        adapter.setResults(items)
    }

    private fun showEmpty() {
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
    }
}