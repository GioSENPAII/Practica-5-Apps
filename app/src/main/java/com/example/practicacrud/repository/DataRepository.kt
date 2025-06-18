package com.example.practicacrud.repository

import android.content.Context
import android.util.Log
import com.example.practicacrud.api.ApiClients
import com.example.practicacrud.database.AppDatabase
import com.example.practicacrud.database.entities.*
import com.example.practicacrud.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val bookDao = database.bookDao()
    private val showDao = database.showDao()
    private val favoriteDao = database.favoriteDao()
    private val searchHistoryDao = database.searchHistoryDao()

    private val openLibraryService = ApiClients.openLibraryService
    private val tvMazeService = ApiClients.tvMazeService

    // Búsqueda de libros con caché
    suspend fun searchBooks(query: String, userId: Int): List<Book> = withContext(Dispatchers.IO) {
        try {
            // Guardar en historial
            searchHistoryDao.insertSearch(
                SearchHistoryEntity(
                    userId = userId,
                    query = query,
                    searchType = "book"
                )
            )

            // Buscar en API
            val response = openLibraryService.searchBooks(query)

            // Guardar en caché local
            val bookEntities = response.docs.map { book ->
                BookEntity(
                    id = book.key,
                    title = book.title,
                    author = book.author_name?.joinToString(", "),
                    coverUrl = book.getCoverUrl(),
                    publishYear = book.first_publish_year,
                    description = null
                )
            }
            bookDao.insertBooks(bookEntities)

            response.docs
        } catch (e: Exception) {
            Log.e("DataRepository", "Error searching books online", e)
            // Si falla, buscar en caché local
            val cachedBooks = bookDao.getAllBooks()
            cachedBooks.filter { it.title.contains(query, ignoreCase = true) }
                .map { entity ->
                    Book(
                        key = entity.id,
                        title = entity.title,
                        author_name = entity.author?.split(", "),
                        cover_i = null,
                        first_publish_year = entity.publishYear,
                        isbn = null,
                        publisher = null,
                        language = null,
                        subject = null
                    )
                }
        }
    }

    // Búsqueda de series con caché
    suspend fun searchShows(query: String, userId: Int): List<Show> = withContext(Dispatchers.IO) {
        try {
            // Guardar en historial
            searchHistoryDao.insertSearch(
                SearchHistoryEntity(
                    userId = userId,
                    query = query,
                    searchType = "show"
                )
            )

            // Buscar en API
            val results = tvMazeService.searchShows(query)

            // Guardar en caché local
            val showEntities = results.map { result ->
                val show = result.show
                ShowEntity(
                    id = show.id,
                    name = show.name,
                    type = show.type,
                    language = show.language,
                    genres = show.genres?.joinToString(","),
                    status = show.status,
                    runtime = show.runtime,
                    premiered = show.premiered,
                    imageUrl = show.image?.medium,
                    summary = show.summary
                )
            }
            showDao.insertShows(showEntities)

            results.map { it.show }
        } catch (e: Exception) {
            Log.e("DataRepository", "Error searching shows online", e)
            // Si falla, buscar en caché local
            val cachedShows = showDao.getAllShows()
            cachedShows.filter { it.name.contains(query, ignoreCase = true) }
                .map { entity ->
                    Show(
                        id = entity.id,
                        url = null,
                        name = entity.name,
                        type = entity.type,
                        language = entity.language,
                        genres = entity.genres?.split(","),
                        status = entity.status,
                        runtime = entity.runtime,
                        premiered = entity.premiered,
                        ended = null,
                        officialSite = null,
                        schedule = null,
                        rating = null,
                        weight = null,
                        network = null,
                        webChannel = null,
                        dvdCountry = null,
                        externals = null,
                        image = entity.imageUrl?.let { Image(medium = it, original = it) },
                        summary = entity.summary,
                        updated = entity.lastUpdated,
                        _links = null
                    )
                }
        }
    }

    // Gestión de favoritos
    suspend fun addToFavorites(userId: Int, itemId: String, itemType: String,
                               title: String, subtitle: String?, imageUrl: String?) {
        withContext(Dispatchers.IO) {
            val favorite = FavoriteEntity(
                userId = userId,
                itemId = itemId,
                itemType = itemType,
                title = title,
                subtitle = subtitle,
                imageUrl = imageUrl
            )
            favoriteDao.insertFavorite(favorite)
        }
    }

    suspend fun removeFromFavorites(userId: Int, itemId: String, itemType: String) {
        withContext(Dispatchers.IO) {
            favoriteDao.deleteFavoriteByIds(userId, itemId, itemType)
        }
    }

    suspend fun isFavorite(userId: Int, itemId: String, itemType: String): Boolean {
        return withContext(Dispatchers.IO) {
            favoriteDao.getFavorite(userId, itemId, itemType) != null
        }
    }

    suspend fun getUserFavorites(userId: Int): List<FavoriteEntity> {
        return withContext(Dispatchers.IO) {
            favoriteDao.getUserFavorites(userId)
        }
    }

    suspend fun getAllFavorites(): List<FavoriteEntity> {
        return withContext(Dispatchers.IO) {
            favoriteDao.getAllFavorites()
        }
    }

    // Historial de búsquedas
    suspend fun getUserSearchHistory(userId: Int): List<SearchHistoryEntity> {
        return withContext(Dispatchers.IO) {
            searchHistoryDao.getUserSearchHistory(userId)
        }
    }

    suspend fun getAllSearchHistory(): List<SearchHistoryEntity> {
        return withContext(Dispatchers.IO) {
            searchHistoryDao.getAllSearchHistory()
        }
    }

    suspend fun clearUserSearchHistory(userId: Int) {
        withContext(Dispatchers.IO) {
            searchHistoryDao.clearUserHistory(userId)
        }
    }

    // Limpiar caché antiguo (más de 7 días)
    suspend fun clearOldCache() {
        withContext(Dispatchers.IO) {
            val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
            bookDao.deleteOldBooks(sevenDaysAgo)
            showDao.deleteOldShows(sevenDaysAgo)
        }
    }
}