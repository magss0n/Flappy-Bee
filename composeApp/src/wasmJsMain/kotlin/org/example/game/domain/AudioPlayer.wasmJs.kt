package org.example.game.domain

import org.w3c.dom.Audio

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AudioPlayer {
    private val audioElements = mutableMapOf<String, Audio>()

    actual fun playGameOverSound() {
        stopFallingSound()
        playSound("game_over.wav")
    }

    actual fun playJumpSound() {
        stopFallingSound()
        playSound("jump.wav")

    }

    actual fun playFallingSound() {
        playSound("falling.wav")

    }

    actual fun stopFallingSound() {
        stopSound("falling.wav")
    }

    actual fun playGameSoundInLoop() {
        playSound("game_sound.wav", true)
    }

    actual fun stopGameSound() {
        playGameOverSound()
        stopSound("game_sound.wav")
    }

    actual fun release() {
        stopAllSound()
        audioElements.clear()

    }

    private fun stopSound(fileName: String){
        audioElements[fileName]?.let { audio ->
            audio.pause()
            audio.currentTime = 0.0
        }
    }

    private fun stopAllSound(){
        audioElements.values.forEach { audio ->
            audio.pause()
            audio.currentTime = 0.0
        }
    }

    private fun playSound(fileName: String, loop: Boolean = false){
        val audio = audioElements[fileName] ?: createAudioElement(fileName).also{
            audioElements[fileName] = it
        }
        audio.loop = loop
        audio.play().catch {
            println("Error playing sound: $fileName")
            it
        }
    }

    private fun createAudioElement(fileName: String): Audio{
        val path = "src/commonMain/composeResources/files/$fileName"
        return Audio(path).apply {
            onerror = { _, _, _, _, _->
                println("Error loading audio file: $path")
                null

            }
        }
    }
}