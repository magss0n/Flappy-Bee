package org.example.game.domain

import androidx.compose.runtime.mutableStateMapOf
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Paths
import javax.sound.sampled.SourceDataLine

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AudioPlayer {

    private val audioCache: MutableMap<String, ByteArray> = mutableStateMapOf()
    private val playingLines = mutableStateMapOf<String, SourceDataLine>()

    actual fun playGameOverSound() {
    }

    actual fun playJumpSound() {
    }

    actual fun playFallingSound() {
    }

    actual fun stopFallingSound() {
    }

    actual fun playGameSoundInLoop() {
    }

    actual fun stopGameSound() {
    }

    actual fun release() {
    }

    private fun loadAudioFile(filename: String): ByteArray{
        val resourcePath = Paths.get("src/commonMain/composeResources/files/$filename")
        if (!Files.exists(resourcePath)){
            throw FileNotFoundException("Resource not found: $resourcePath")
        }
        return FileInputStream(resourcePath.toFile()).use{it.readBytes()}
    }
}