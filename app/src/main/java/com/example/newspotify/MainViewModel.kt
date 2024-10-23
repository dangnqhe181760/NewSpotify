package com.example.imageapp

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.imageapp.Extension.makeResult
import com.example.newspotify.TopTracksResponse
import com.spotify.protocol.types.Track
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MainViewModel: ViewModel() {
    private var ioJob: Job = Job()
    val coroutineScope by lazy { CoroutineScope(ioJob + Dispatchers.IO + exceptionHandler) }
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, error ->
        Log.d("exceptionHandler", "${error.message}")
    }

    fun fetchTopTracks(accessToken: String) = coroutineScope.launch {
        val response = apiService.getTopTracks(accessToken)
        if (!response.items.isEmpty()) {
            response?.let { trackValue.value = response }
        } else {
            trackValue.value = TopTracksResponse(emptyList(), 0, 0, 0, "", null, null) // Set to an empty list if no data is returned
            errorResponse.emit("No track found.") // Emit an error message
        }
    }

    private val errorResponse = MutableStateFlow<String?>(null)

    private val trackValue = MutableStateFlow(TopTracksResponse(emptyList(), 0, 0, 0, "", null, null))

    val trackResponse: StateFlow<TopTracksResponse?> =
        trackValue.makeResult(coroutineScope)

    private val apiService = RetrofitInstance.api
}