package com.example.newspotify

import com.google.gson.annotations.SerializedName
import com.spotify.protocol.types.Album
import com.spotify.protocol.types.Artist

data class Track(
    @SerializedName("artist") val artist: Artist,
    @SerializedName("artists") val artists: List<Artist>,
    @SerializedName("album") val album: Album,
    @SerializedName("duration_ms") val duration: Long,
    @SerializedName("name") val name: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("images") val images: List<Image>, // List of Image objects
    @SerializedName("is_episode") val isEpisode: Boolean,
    @SerializedName("is_podcast") val isPodcast: Boolean
)
