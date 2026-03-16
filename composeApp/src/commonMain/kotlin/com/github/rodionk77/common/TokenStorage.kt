package com.github.rodionk77.common

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.liftric.kvault.KVault
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class TokenStorage(private val kVault: KVault) {

    companion object {
        private const val TOKEN_KEY = "github_access_token"
        private const val REFRESH_TOKEN_KEY = "github_refresh_token"
    }

    fun saveToken(token: String) {
        kVault.set(TOKEN_KEY, token)
    }

    fun getToken(): String? {
        val token = kVault.string(TOKEN_KEY)
        Napier.d("getToken: $token", tag = "TokenStorage")
        return token
    }

    fun clearToken() {
        kVault.deleteObject(TOKEN_KEY)
    }

    fun saveRefreshToken(token: String) {
        kVault.set(REFRESH_TOKEN_KEY, token)
    }

    fun getRefreshToken(): String? {
        return kVault.string(REFRESH_TOKEN_KEY)
    }

    fun clearRefreshToken() {
        kVault.deleteObject(REFRESH_TOKEN_KEY)
    }

    fun clearAll() {
        clearToken()
        clearRefreshToken()
    }

}