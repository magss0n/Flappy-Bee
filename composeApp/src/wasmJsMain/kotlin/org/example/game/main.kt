package org.example.game

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.example.game.di.initializeKotlin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initializeKotlin()
    ComposeViewport(document.body!!) {
        App()
    }
}