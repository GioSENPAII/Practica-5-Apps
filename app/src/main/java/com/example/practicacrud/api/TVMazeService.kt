package com.example.practicacrud.api

import com.example.practicacrud.models.Show
import com.example.practicacrud.models.ShowSearchResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TVMazeService {
    @GET("search/shows")
    suspend fun searchShows(@Query("q") query: String): List<ShowSearchResult>

    @GET("shows/{id}")
    suspend fun getShow(@Path("id") id: Int): Show

    @GET("shows")
    suspend fun getShows(@Query("page") page: Int = 0): List<Show>
}