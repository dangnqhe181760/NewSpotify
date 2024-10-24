package com.example.newspotify

import com.google.gson.annotations.SerializedName

data class Album(
    @SerializedName("name") val name: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("images") val images: List<Image>
)
