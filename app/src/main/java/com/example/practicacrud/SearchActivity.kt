package com.example.practicacrud

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practicacrud.adapters.SearchResultsAdapter
import com.example.practicacrud.repository.DataRepository
import com.example.practicacrud.utils.AuthManager
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView

    private lateinit var authManager: AuthManager
    private lateinit var repository: DataRepository
    private lateinit var adapter: SearchResultsAdapter

    private var currentSearchType = "books" // "books" o "shows"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        authManager = AuthManager(this)
        repository = DataRepository(this)

        // Verificar que el usuario esté autenticado
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "Debe iniciar sesión para buscar", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupTabs()
        setupSearch()
        setupRecyclerView()
    }

    private fun initViews() {
        searchView = findViewById(R.id.searchView)
        tabLayout = findViewById(R.id.tabLayout)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyView = findViewById(R.id.emptyView)
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Libros"))
        tabLayout.addTab(tabLayout.newTab().setText("Series/Películas"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentSearchType = when (tab?.position) {
                    0 -> "books"
                    1 -> "shows"
                    else -> "books"
                }
                // Limpiar resultados al cambiar de tab
                adapter.clearResults()
                emptyView.visibility = View.VISIBLE
                emptyView.text = "Busca ${if (currentSearchType == "books") "libros" else "series"}"
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Opcionalmente, podrías implementar búsqueda en tiempo real
                return false
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = SearchResultsAdapter(
            onItemClick = { item ->
                // Manejar clic en item
                Toast.makeText(this, "Seleccionado: ${item.title}", Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { item ->
                toggleFavorite(item)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun performSearch(query: String) {
        lifecycleScope.launch {
            showLoading()

            try {
                val userId = authManager.getUserId()

                when (currentSearchType) {
                    "books" -> {
                        val books = repository.searchBooks(query, userId)
                        if (books.isEmpty()) {
                            showEmpty("No se encontraron libros")
                        } else {
                            val searchItems = books.map { book ->
                                SearchResultsAdapter.SearchItem(
                                    id = book.key,
                                    title = book.title,
                                    subtitle = book.author_name?.joinToString(", ") ?: "Autor desconocido",
                                    imageUrl = book.getCoverUrl(),
                                    type = "book",
                                    isFavorite = repository.isFavorite(userId, book.key, "book")
                                )
                            }
                            showResults(searchItems)
                        }
                    }
                    "shows" -> {
                        val shows = repository.searchShows(query, userId)
                        if (shows.isEmpty()) {
                            showEmpty("No se encontraron series/películas")
                        } else {
                            val searchItems = shows.map { show ->
                                SearchResultsAdapter.SearchItem(
                                    id = show.id.toString(),
                                    title = show.name,
                                    subtitle = "${show.type ?: "Show"} - ${show.premiered?.substring(0, 4) ?: "Año desconocido"}",
                                    imageUrl = show.image?.medium,
                                    type = "show",
                                    isFavorite = repository.isFavorite(userId, show.id.toString(), "show")
                                )
                            }
                            showResults(searchItems)
                        }
                    }
                }
            } catch (e: Exception) {
                showError("Error al buscar: ${e.message}")
            }
        }
    }

    private fun toggleFavorite(item: SearchResultsAdapter.SearchItem) {
        lifecycleScope.launch {
            try {
                val userId = authManager.getUserId()

                if (item.isFavorite) {
                    repository.removeFromFavorites(userId, item.id, item.type)
                    Toast.makeText(this@SearchActivity, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    repository.addToFavorites(
                        userId = userId,
                        itemId = item.id,
                        itemType = item.type,
                        title = item.title,
                        subtitle = item.subtitle,
                        imageUrl = item.imageUrl
                    )
                    Toast.makeText(this@SearchActivity, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                }

                // Actualizar estado del item
                item.isFavorite = !item.isFavorite
                adapter.updateItem(item)

            } catch (e: Exception) {
                Toast.makeText(this@SearchActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.GONE
    }

    private fun showResults(items: List<SearchResultsAdapter.SearchItem>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        adapter.setResults(items)
    }

    private fun showEmpty(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
        emptyView.text = message
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
        emptyView.text = message
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}