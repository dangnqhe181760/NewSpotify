package com.example.newspotify

import com.spotify.protocol.types.Track

data class TopTracksResponse(
    val items: List<Track>, // This is the list of tracks you care about
    val total: Int,
    val limit: Int,
    val offset: Int,
    val href: String,
    val previous: String?,
    val next: String?
)
