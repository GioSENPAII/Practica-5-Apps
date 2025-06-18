// Crear en app/src/main/java/com/example/practicacrud/services/RecommendationService.kt
package com.example.practicacrud.services

import android.content.Context
import com.example.practicacrud.database.AppDatabase
import com.example.practicacrud.database.entities.FavoriteEntity
import com.example.practicacrud.database.entities.SearchHistoryEntity
import com.example.practicacrud.models.Book
import com.example.practicacrud.models.Show
import com.example.practicacrud.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecommendationService(context: Context) {
    private val repository = DataRepository(context)
    private val database = AppDatabase.getDatabase(context)

    data class Recommendation(
        val id: String,
        val title: String,
        val subtitle: String,
        val imageUrl: String?,
        val type: String,
        val reason: String // Por qué se recomienda
    )

    suspend fun getRecommendations(userId: Int): List<Recommendation> = withContext(Dispatchers.IO) {
        val recommendations = mutableListOf<Recommendation>()

        // Obtener favoritos e historial del usuario
        val favorites = repository.getUserFavorites(userId)
        val searchHistory = repository.getUserSearchHistory(userId)

        // Analizar patrones en favoritos
        val favoriteGenres = extractGenresFromFavorites(favorites)
        val favoriteAuthors = extractAuthorsFromFavorites(favorites)

        // Analizar términos de búsqueda más frecuentes
        val frequentSearchTerms = getFrequentSearchTerms(searchHistory)

        // Generar recomendaciones basadas en favoritos
        if (favorites.isNotEmpty()) {
            // Buscar libros similares a los favoritos
            favorites.filter { it.itemType == "book" }.take(3).forEach { favorite ->
                try {
                    val similarBooks = repository.searchBooks(
                        favorite.subtitle?.split(",")?.firstOrNull() ?: favorite.title,
                        userId
                    )
                    similarBooks.filter { book ->
                        book.key != favorite.itemId
                    }.take(2).forEach { book ->
                        recommendations.add(
                            Recommendation(
                                id = book.key,
                                title = book.title,
                                subtitle = book.author_name?.joinToString(", ") ?: "Autor desconocido",
                                imageUrl = book.getCoverUrl(),
                                type = "book",
                                reason = "Basado en tu interés en '${favorite.title}'"
                            )
                        )
                    }
                } catch (e: Exception) {
                    // Ignorar errores individuales
                }
            }

            // Buscar shows similares
            favorites.filter { it.itemType == "show" }.take(3).forEach { favorite ->
                try {
                    // Usar palabras clave del título para buscar shows similares
                    val keywords = favorite.title.split(" ").filter { it.length > 3 }
                    keywords.forEach { keyword ->
                        val similarShows = repository.searchShows(keyword, userId)
                        similarShows.filter { show ->
                            show.id.toString() != favorite.itemId
                        }.take(1).forEach { show ->
                            recommendations.add(
                                Recommendation(
                                    id = show.id.toString(),
                                    title = show.name,
                                    subtitle = "${show.type ?: "Show"} - ${show.genres?.joinToString(", ") ?: ""}",
                                    imageUrl = show.image?.medium,
                                    type = "show",
                                    reason = "Similar a '${favorite.title}'"
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Ignorar errores individuales
                }
            }
        }

        // Generar recomendaciones basadas en búsquedas frecuentes
        frequentSearchTerms.take(3).forEach { term ->
            try {
                // Alternar entre libros y shows
                if (recommendations.count { it.type == "book" } <= recommendations.count { it.type == "show" }) {
                    val books = repository.searchBooks(term, userId)
                    books.take(1).forEach { book ->
                        recommendations.add(
                            Recommendation(
                                id = book.key,
                                title = book.title,
                                subtitle = book.author_name?.joinToString(", ") ?: "Autor desconocido",
                                imageUrl = book.getCoverUrl(),
                                type = "book",
                                reason = "Relacionado con tus búsquedas de '$term'"
                            )
                        )
                    }
                } else {
                    val shows = repository.searchShows(term, userId)
                    shows.take(1).forEach { show ->
                        recommendations.add(
                            Recommendation(
                                id = show.id.toString(),
                                title = show.name,
                                subtitle = "${show.type ?: "Show"}",
                                imageUrl = show.image?.medium,
                                type = "show",
                                reason = "Relacionado con tus búsquedas de '$term'"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Ignorar errores individuales
            }
        }

        // Eliminar duplicados y limitar a 10 recomendaciones
        recommendations.distinctBy { "${it.id}-${it.type}" }.take(10)
    }

    private fun extractGenresFromFavorites(favorites: List<FavoriteEntity>): List<String> {
        // Extraer géneros de los subtítulos (principalmente para shows)
        return favorites.mapNotNull { it.subtitle }
            .flatMap { it.split(",", "-") }
            .map { it.trim() }
            .groupBy { it }
            .map { it.key to it.value.size }
            .sortedByDescending { it.second }
            .map { it.first }
    }

    private fun extractAuthorsFromFavorites(favorites: List<FavoriteEntity>): List<String> {
        // Extraer autores de los favoritos de libros
        return favorites.filter { it.itemType == "book" }
            .mapNotNull { it.subtitle }
            .flatMap { it.split(",") }
            .map { it.trim() }
            .distinct()
    }

    private fun getFrequentSearchTerms(searchHistory: List<SearchHistoryEntity>): List<String> {
        // Obtener los términos de búsqueda más frecuentes
        return searchHistory.map { it.query }
            .groupBy { it }
            .map { it.key to it.value.size }
            .sortedByDescending { it.second }
            .map { it.first }
    }
}