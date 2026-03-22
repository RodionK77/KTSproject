package com.github.rodionk77.feature.repoDescription.domain

import com.github.rodionk77.feature.repoDescription.data.GitHubContentItem
import com.github.rodionk77.feature.repoDescription.data.RepoDescriptionEntity

interface RepoDescriptionRepository {
    suspend fun getRepository(ownerLogin: String, repoName: String): Result<RepoDescriptionEntity>
    suspend fun getReadme(ownerLogin: String, repoName: String): Result<String>
    suspend fun saveReadme(ownerLogin: String, repoName: String, content: String)
    suspend fun createIssue(ownerLogin: String, repoName: String, title: String, body: String): Result<Unit>
    suspend fun getContents(ownerLogin: String, repoName: String, path: String): Result<List<GitHubContentItem>>
    suspend fun getFileContent(downloadUrl: String): Result<String>
}