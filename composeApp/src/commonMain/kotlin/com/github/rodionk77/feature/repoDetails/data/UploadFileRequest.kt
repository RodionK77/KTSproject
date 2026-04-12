package com.github.rodionk77.feature.repoDetails.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadFileRequest(
    @SerialName("message") val message: String,
    @SerialName("content") val content: String,
    @SerialName("sha") val sha: String? = null
)
