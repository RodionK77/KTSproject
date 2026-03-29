package com.github.rodionk77.feature.repoDetails.data

import com.github.rodionk77.common.Utils.HttpException
import com.github.rodionk77.feature.repoDetails.data.room.RepoDescriptionDao
import com.github.rodionk77.feature.repoDetails.data.room.toDbEntity
import com.github.rodionk77.feature.repoDetails.data.room.toDomainEntity
import com.github.rodionk77.feature.repoDetails.domain.RepoDetailsRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPath
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class RepoDetailsRepositoryImpl(
    private val httpClient: HttpClient,
    private val repoDescriptionDao: RepoDescriptionDao
) : RepoDetailsRepository {

    override suspend fun getRepository(ownerLogin: String, repoName: String): Result<RepoDetailsEntity> {
        return try {
            val response = httpClient.get("repos/$ownerLogin/$repoName")
            val repo: RepoDetailsEntity = response.body()
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
    override suspend fun getReadme(ownerLogin: String, repoName: String): Result<String> {
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

    override suspend fun saveReadme(ownerLogin: String, repoName: String, content: String) {
        repoDescriptionDao.updateReadme(repoName, ownerLogin, content)
    }

    override suspend fun createIssue(ownerLogin: String, repoName: String, title: String, body: String): Result<Unit> {
        return try {
            val response = httpClient.post("repos/$ownerLogin/$repoName/issues") {
                contentType(ContentType.Application.Json)
                setBody(CreateIssueRequest(title, body))
            }
            if (response.status.value !in 200..299) {
                throw HttpException(response.status.value)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getContents(
        ownerLogin: String,
        repoName: String,
        path: String
    ): Result<List<GitHubContentItem>> {
        return try {
            val pathSegment = if (path.isEmpty()) "" else "/$path"
            val response = httpClient.get("repos/$ownerLogin/$repoName/contents$pathSegment")
            val items: List<GitHubContentItem> = response.body()
            val sorted = items.sortedWith(
                compareBy<GitHubContentItem> { if (it.isDirectory) 0 else 1 }
                    .thenBy { it.name.lowercase() }
            )
            Result.success(sorted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFileContent(downloadUrl: String): Result<String> {
        return try {
            val response = httpClient.get(downloadUrl)
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun uploadFile(
        ownerLogin: String,
        repoName: String,
        filePath: String,
        fileName: String,
        fileBytes: ByteArray,
        existingSha: String?,
        message: String
    ): Result<Unit> {
        return try {
            val base64Content = Base64.encode(fileBytes)
            val requestBody = UploadFileRequest(
                message = message,
                content = base64Content,
                sha = existingSha?.ifEmpty { null }
            )
            val encodedPath = filePath.encodeURLPath()
            val response = httpClient.put("repos/$ownerLogin/$repoName/contents/$encodedPath") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            if (response.status.value !in 200..299) {
                throw HttpException(response.status.value)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
