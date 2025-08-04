package org.example.game

import androidx.compose.ui.window.ComposeUIViewController
import org.example.game.di.initializeKotlin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKotlin() }
) { App() }