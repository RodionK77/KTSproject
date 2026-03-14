package com.github.rodionk77.feature.repoDescription.data

import com.github.rodionk77.feature.repoDescription.data.room.RepoDescriptionDao
import com.github.rodionk77.feature.repoDescription.data.room.toDbEntity
import com.github.rodionk77.feature.repoDescription.data.room.toDomainEntity
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class RepoDescriptionRepository (
    private val httpClient: HttpClient,
    private val repoDescriptionDao: RepoDescriptionDao
){

    suspend fun getRepository(ownerLogin: String, repoName: String): Result<RepoDescriptionEntity> {
        return try {
            val response = httpClient.get("repos/$ownerLogin/$repoName")
            val repo: RepoDescriptionEntity = response.body()
            repoDescriptionDao.upsert(repo.toDbEntity())
            Result.success(repo)
        } catch (e: Exception) {
            val cached = repoDescriptionDao.get(repoName, ownerLogin)
            if (cached != null) {
                Result.success(cached.toDomainEntity())
            } else {
                Result.failure(e)
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun getReadme(ownerLogin: String, repoName: String): Result<String> {
        return try {
            val response = httpClient.get("repos/$ownerLogin/$repoName/readme")
            val entity: ReadmeEntity = response.body()
            val decoded = Base64.decode(entity.content.replace("\n", "")).decodeToString()
            val baseUrl = entity.downloadUrl?.substringBeforeLast("/")?.plus("/")
            Result.success(preprocessMarkdown(decoded, baseUrl))
        } catch (e: Exception) {
            val cached = repoDescriptionDao.get(repoName, ownerLogin)?.readmeContent
            if (cached != null) Result.success(cached) else Result.failure(e)
        }
    }

    suspend fun saveReadme(ownerLogin: String, repoName: String, content: String) {
        repoDescriptionDao.updateReadme(repoName, ownerLogin, content)
    }

    private fun preprocessMarkdown(markdown: String, baseUrl: String?): String {
        val withAbsoluteUrls = if (baseUrl != null) {
            markdown.replace(Regex("""!\[([^\]]*)\]\((?!https?://)([^)]+)\)""")) { match ->
                "![${match.groupValues[1]}](${baseUrl}${match.groupValues[2]})"
            }
        } else markdown

        return withAbsoluteUrls.replace(
            Regex("""(?m)^(!\[[^\]]*\]\([^)]+\))\s*$""")
        ) { match ->
            "\n${match.groupValues[1]}\n"
        }
    }
}