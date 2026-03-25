package com.github.rodionk77.feature.repoDetails.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubContentItem(
    @SerialName("name") val name: String,
    @SerialName("path") val path: String,
    @SerialName("type") val type: String,
    @SerialName("size") val size: Int = 0,
    @SerialName("download_url") val downloadUrl: String? = null,
    @SerialName("sha") val sha: String = ""
) {
    val isDirectory: Boolean get() = type == "dir"
    val isFile: Boolean get() = type == "file"
}
