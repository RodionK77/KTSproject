package com.github.rodionk77.feature.repoDescription.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadmeEntity(
    @SerialName("content") val content: String = "",
    @SerialName("encoding") val encoding: String = "",
    @SerialName("download_url") val downloadUrl: String? = null
)
