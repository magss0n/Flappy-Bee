package org.example.game.util

enum class Platform{
    Android,
    iOS,
    Desktop,
    Web
}

expect fun getPlatform(): Platform