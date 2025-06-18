// Crear en app/src/main/java/com/example/practicacrud/HistoryActivity.kt
package com.example.practicacrud

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practicacrud.adapters.HistoryAdapter
import com.example.practicacrud.repository.DataRepository
import com.example.practicacrud.utils.AuthManager
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView

    private lateinit var authManager: AuthManager
    private lateinit var repository: DataRepository
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        authManager = AuthManager(this)
        repository = DataRepository(this)

        // Verificar que el usuario esté autenticado
        if (!authManager.isLoggedIn()) {
            Toast.makeText(this, "Debe iniciar sesión para ver el historial", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupRecyclerView()
        loadHistory()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_history -> {
                showClearHistoryDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        emptyView = findViewById(R.id.emptyView)
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter { searchHistory ->
            // Realizar nueva búsqueda con el término seleccionado
            Toast.makeText(this, "Búsqueda: ${searchHistory.query}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadHistory() {
        lifecycleScope.launch {
            try {
                val userId = authManager.getUserId()
                val isAdmin = authManager.isAdmin()

                // Si es admin, puede ver todo el historial
                val history = if (isAdmin) {
                    repository.getAllSearchHistory()
                } else {
                    repository.getUserSearchHistory(userId)
                }

                if (history.isEmpty()) {
                    showEmpty()
                } else {
                    showHistory()
                    adapter.setHistory(history)
                }
            } catch (e: Exception) {
                Toast.makeText(this@HistoryActivity, "Error al cargar historial: ${e.message}", Toast.LENGTH_LONG).show()
                showEmpty()
            }
        }
    }

    private fun showClearHistoryDialog() {
        AlertDialog.Builder(this)
            .setTitle("Limpiar historial")
            .setMessage("¿Estás seguro de que deseas eliminar todo tu historial de búsquedas?")
            .setPositiveButton("Sí") { _, _ ->
                clearHistory()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun clearHistory() {
        lifecycleScope.launch {
            try {
                val userId = authManager.getUserId()
                repository.clearUserSearchHistory(userId)
                Toast.makeText(this@HistoryActivity, "Historial eliminado", Toast.LENGTH_SHORT).show()
                loadHistory()
            } catch (e: Exception) {
                Toast.makeText(this@HistoryActivity, "Error al limpiar historial: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showHistory() {
        recyclerView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
    }

    private fun showEmpty() {
        recyclerView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
    }
}