package com.github.rodionk77.feature.login.domain

interface AuthRepository {
    suspend fun exchangeCodeForToken(code: String): Result<String>
    suspend fun refreshAccessToken(): Result<String>
    fun saveToken(token: String)
    fun getToken(): String?
    fun hasSeenWelcome(): Boolean
    fun markWelcomeSeen()
}