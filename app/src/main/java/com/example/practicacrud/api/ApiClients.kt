package com.example.practicacrud.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClients {
    private const val OPEN_LIBRARY_BASE_URL = "https://openlibrary.org/"
    private const val TVMAZE_BASE_URL = "https://api.tvmaze.com/"

    val openLibraryService: OpenLibraryService by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_LIBRARY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenLibraryService::class.java)
    }

    val tvMazeService: TVMazeService by lazy {
        Retrofit.Builder()
            .baseUrl(TVMAZE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TVMazeService::class.java)
    }
}