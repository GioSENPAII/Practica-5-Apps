package com.example.practicacrud.models

// Modelos para Open Library API
data class BookSearchResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<Book>
)

data class Book(
    val key: String,
    val title: String,
    val author_name: List<String>?,
    val cover_i: Int?,
    val first_publish_year: Int?,
    val isbn: List<String>?,
    val publisher: List<String>?,
    val language: List<String>?,
    val subject: List<String>?
) {
    fun getCoverUrl(size: String = "M"): String? {
        return cover_i?.let { "https://covers.openlibrary.org/b/id/$it-$size.jpg" }
    }
}

data class AuthorSearchResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<Author>
)

data class Author(
    val key: String,
    val name: String,
    val birth_date: String?,
    val death_date: String?,
    val work_count: Int?,
    val top_work: String?,
    val top_subjects: List<String>?
)