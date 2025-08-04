package org.example.game

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.game.di.initializeKotlin

fun main() = application {
    initializeKotlin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "FlappyBee",
    ) {
        App()
    }
}