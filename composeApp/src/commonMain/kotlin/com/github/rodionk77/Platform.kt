package com.github.rodionk77

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform