package com.github.rodionk77.feature.repos.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerialName("login") val login: String = "...",
    @SerialName("avatar_url") val avatarUrl: String = "",
    @SerialName("name") val name: String? = null,          // полное имя
    @SerialName("bio") val bio: String? = null,             // описание профиля
    @SerialName("email") val email: String? = null,         // email если публичный
    @SerialName("location") val location: String? = null,   // местоположение
    @SerialName("company") val company: String? = null,     // компания
    @SerialName("blog") val blog: String? = null,           // сайт
    @SerialName("twitter_username") val twitterUsername: String? = null,
    @SerialName("public_repos") val publicRepos: Int = 0,   // кол-во публичных репо
    @SerialName("followers") val followers: Int = 0,        // подписчики
    @SerialName("following") val following: Int = 0,        // подписки
    @SerialName("created_at") val createdAt: String? = null // дата регистрации
)