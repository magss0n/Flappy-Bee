package org.example.game.di

import org.example.game.domain.AudioPlayer
import org.koin.core.module.Module
import org.koin.dsl.module

actual val targetModule= module {
    single<AudioPlayer> { AudioPlayer() }
}