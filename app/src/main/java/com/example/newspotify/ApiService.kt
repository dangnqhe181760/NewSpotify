package com.example.imageapp

import com.example.newspotify.TopTracksResponse
import com.spotify.protocol.types.Track
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("v1/me/top/tracks")
    suspend fun getTopTracks(
        @Header("Authorization") accessToken: String,
        @Query("limit") limit: Int = 5
    ): TopTracksResponse
}