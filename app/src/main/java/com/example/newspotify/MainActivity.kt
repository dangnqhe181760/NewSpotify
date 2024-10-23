package com.example.newspotify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imageapp.MainViewModel
import com.example.newspotify.ui.theme.NewSpotifyTheme
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

private val clientId = "b8ece90116fd47898d863c0e0112ddd8"
private val redirectUri = "com.example.newspotify://callback"
private var spotifyAppRemote: SpotifyAppRemote? = null
private val REQUEST_CODE = 1337
private var accessToken = ""
private val scopes = arrayOf(
    "app-remote-control",
    "user-modify-playback-state",
    "user-top-read",             // To read user's top tracks
    "playlist-read-private",     // To read user's private playlists
    "playlist-modify-public",    // To modify user's public playlists
    "user-read-playback-state",  // To read the user's current playback state
    "user-read-recently-played"  // To access recently played tracks
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val builder = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )
        builder.setScopes(scopes)
        val request = builder.build()

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    // Handle successful authentication
                    accessToken = response.accessToken
                    connectToSpotifyAppRemote(accessToken)

                    // Set content after token is obtained
                    setContent {
                        NewSpotifyTheme {
                            Log.d("Token", "$accessToken")
                            HomePage()
                        }
                    }
                }
                AuthorizationResponse.Type.ERROR -> {
                    // Handle error
                    Log.e("MainActivity", "Auth error: ${response.error}")
                }
                else -> {
                    // Handle other cases
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Spotify remote connection is handled after authorization
    }

    private fun connectToSpotifyAppRemote(accessToken: String) {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Handle errors
            }
        })
    }

    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }
}

@Composable
fun HomePage() {
    val viewModel: MainViewModel = viewModel()
    val observedTracks by viewModel.trackResponse.collectAsState(initial = TopTracksResponse(emptyList(), 0, 0, 0, "", null, null))
    viewModel.fetchTopTracks("Bearer $accessToken")
    Log.d("Top tracks: ", observedTracks.toString())
//    observedContacts?.let { ImageGrid(contacts = it) }
}