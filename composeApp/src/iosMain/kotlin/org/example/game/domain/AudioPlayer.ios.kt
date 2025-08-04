package org.example.game.domain

import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.Foundation.NSURL.Companion.fileURLWithPath

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class AudioPlayer {

    private var audioPlayer: MutableMap<String, AVAudioPlayer?> = mutableMapOf() //AVAudioPlayer is the player specific to iOS target

    private var fallingSoundPlayer: AVAudioPlayer ? = null

    init{
//        Configure the audio session for playback
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, null)
        session.setActive(true, null)
    }
    actual fun playGameOverSound() {
        stopFallingSound()
        playSound("game_over")
    }

    actual fun playJumpSound() {
        stopFallingSound()
        playSound("jump")
    }

    actual fun playFallingSound() {
        fallingSoundPlayer = playSound("falling")
    }

    actual fun stopFallingSound() {
        fallingSoundPlayer?.stop()
        fallingSoundPlayer = null
    }

    actual fun playGameSoundInLoop() {
        val url = getSoundURL("game_sound")
        val player = url?.let {AVAudioPlayer(it, null)}
        player?.numberOfLoops = -1
        player?.prepareToPlay()
        player?.play()
        audioPlayer["game_sound"] = player
    }

    actual fun stopGameSound() {
        audioPlayer["game_sound"]?.stop()
        playGameOverSound()
        audioPlayer["game_sound"] = null

    }

    actual fun release() {
        audioPlayer.values.forEach { it?.stop() }
        audioPlayer.clear()
        fallingSoundPlayer?.stop()
        fallingSoundPlayer = null
    }

    private fun playSound(soundName: String): AVAudioPlayer?{
        val url = getSoundURL(soundName)
        val player = url?.let { AVAudioPlayer(it, null) }
        player?.prepareToPlay()
        player?.play()
        //Store the player for future management
        audioPlayer[soundName] = player
        return player
    }

    private fun getSoundURL(resourceName: String): NSURL?{
        val bundle = NSBundle.mainBundle()
        val path = bundle.pathForResource(resourceName, "wav")
        return path?.let { fileURLWithPath(it) }
    }
}