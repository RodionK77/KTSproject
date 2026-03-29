package com.github.rodionk77.feature.repoDetails.domain

import com.github.rodionk77.feature.repoDetails.data.GitHubContentItem
import com.github.rodionk77.feature.repoDetails.data.RepoDetailsEntity

interface RepoDetailsRepository {
    suspend fun getRepository(ownerLogin: String, repoName: String): Result<RepoDetailsEntity>
    suspend fun getReadme(ownerLogin: String, repoName: String): Result<String>
    suspend fun saveReadme(ownerLogin: String, repoName: String, content: String)
    suspend fun createIssue(ownerLogin: String, repoName: String, title: String, body: String): Result<Unit>
    suspend fun getContents(ownerLogin: String, repoName: String, path: String): Result<List<GitHubContentItem>>
    suspend fun getFileContent(downloadUrl: String): Result<String>
    suspend fun uploadFile(
        ownerLogin: String,
        repoName: String,
        filePath: String,
        fileName: String,
        fileBytes: ByteArray,
        existingSha: String?,
        message: String
    ): Result<Unit>
}