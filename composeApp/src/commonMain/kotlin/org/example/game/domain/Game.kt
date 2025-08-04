package org.example.game.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.ObservableSettings
import org.example.game.util.Platform
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

const val SCORE_KEY = "score"

data class Game(
    val platform: Platform,
    val screenWidth: Int = 0,
    val screenHeight: Int = 0,
    val beeRadius: Float = 20f,
    val gravity: Float = 0.8f, //Downward force exerted on the bee to fall
    val beeJumpImpulse: Float = -15f, //Upward force  which causes the bee to jump
    val beeMaxVelocity: Float = if (platform == Platform.Android) 25f else 10f, //How fast the bee can move vertically
    val pipeWidth: Float = 200f,
    val pipeVelocity: Float = if (platform == Platform.Android) 5f else 2.5f,
    val pipeGapSize: Float = if (platform == Platform.Android) 250f else 300f
): KoinComponent {
    private val audioPlayer: AudioPlayer by inject()
    private val settings: ObservableSettings by inject()
    var status by mutableStateOf(GameStatus.Idle)
        private set

    var beeVelocity by mutableStateOf(0f)
        private set

    var pipePairs = mutableStateListOf<PipePair>()

    var currentScore by mutableStateOf(0)
        private set

    var bestScore by mutableStateOf(0)
        private set

    private var isFallingSoundPlayed = false

    init {
        bestScore = settings.getInt(
            key = SCORE_KEY,
            defaultValue = 0
        )
        settings.addIntListener(
            key = SCORE_KEY,
            defaultValue = 0
        ){
            bestScore = it
        }
    }

    var bee by mutableStateOf(
        Bee(
            x = screenWidth/ 4f,
            y = screenHeight/2f,
            radius = beeRadius
        )
    )

    fun start(){
        status = GameStatus.Started
        audioPlayer.playGameSoundInLoop()
    }

    fun gameOver(){
        status = GameStatus.Over
        audioPlayer.stopGameSound()
        saveScore()
        isFallingSoundPlayed = false

    }

    private fun saveScore(){
        if (bestScore < currentScore){
            settings.putInt(key = SCORE_KEY, value = currentScore)
            bestScore = currentScore
        }
    }

    fun jump(){
        beeVelocity = beeJumpImpulse
        audioPlayer.playJumpSound()
        isFallingSoundPlayed = false

    }

    private fun resetBeePosition(){
        bee = bee.copy(y = screenHeight/2f)
        beeVelocity = 0f
    }

    private fun removePipes(){
        pipePairs.clear()
    }

    private fun resetScore(){
        currentScore = 0
    }

    fun restart(){
        resetBeePosition()
        removePipes()
        resetScore()
        start()
        isFallingSoundPlayed = false
    }

    fun updateGameProgress(){
        pipePairs.forEach { pipePair ->
            if (hasCollided(pipePair)){
                gameOver()
                return
            }

            if(!pipePair.scored && bee.x > pipePair.x + pipeWidth/2){
                pipePair.scored = true
                currentScore += 1
            }
        }

        if (bee.y<0f){
            stoptheBee()
            return
        } else if (bee.y > screenHeight) {
            gameOver()
            return
        }

        beeVelocity = (beeVelocity+gravity).coerceIn(-beeMaxVelocity, beeMaxVelocity)
        bee = bee.copy(y = bee.y+beeVelocity)

        //When to play the falling sound
        if (beeVelocity > (beeMaxVelocity/1.1)){
            if (!isFallingSoundPlayed){
                audioPlayer.playFallingSound()
                isFallingSoundPlayed = true
            }
        }

        spawnPipes()
    }


    private fun spawnPipes() {
        pipePairs.forEach { it.x -= pipeVelocity }
        pipePairs.removeAll { it.x + pipeWidth<0 }

        if (pipePairs.isEmpty() || pipePairs.last().x < screenWidth/2){
            val initialPipeX =screenWidth.toFloat() + pipeWidth
            val topHeight = Random.nextFloat() * (screenHeight/2)
            val bottomHeight = screenHeight - topHeight - pipeGapSize
            val newPipePair = PipePair(
                x=initialPipeX,
                y=topHeight + pipeGapSize/2,
                topHeight = topHeight,
                bottomHeight = bottomHeight
            )
            pipePairs.add(newPipePair)
        }
    }

    private fun hasCollided(pipePair: PipePair): Boolean{
//        Check horizontal collision. that is Bee overlaps Pipe's X range
        val beeLeftEdge = bee.x-beeRadius
        val beeRightEdge = bee.x+beeRadius

        val pipeLeftEdge = pipePair.x - pipeWidth /2
        val pipeRightEdge = pipePair.x + pipeWidth/2

        val beeInHorizontalGap = beeRightEdge > pipeLeftEdge && beeLeftEdge < pipeRightEdge

        val beeTopEdge = bee.y-beeRadius
        val beeBottomEdge = bee.y+beeRadius

        val gapTopEdge = pipePair.y - pipeGapSize /2
        val gapBottomEdge = pipePair.y + pipeGapSize/2

        val beeInVerticalGap = beeTopEdge > gapTopEdge && beeBottomEdge < gapBottomEdge

        return beeInHorizontalGap && !beeInVerticalGap
    }

    fun stoptheBee(){
        beeVelocity = 0f
        bee = bee.copy(y=0f)
    }

    fun cleanUp(){
        audioPlayer.release()
    }
}

