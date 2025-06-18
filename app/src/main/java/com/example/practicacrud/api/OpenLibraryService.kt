package com.example.practicacrud.api

import com.example.practicacrud.models.AuthorSearchResponse
import com.example.practicacrud.models.BookSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryService {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): BookSearchResponse

    @GET("search.json")
    suspend fun searchBooksByTitle(
        @Query("title") title: String,
        @Query("limit") limit: Int = 20
    ): BookSearchResponse

    @GET("search.json")
    suspend fun searchBooksByAuthor(
        @Query("author") author: String,
        @Query("limit") limit: Int = 20
    ): BookSearchResponse

    @GET("search/authors.json")
    suspend fun searchAuthors(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): AuthorSearchResponse
}