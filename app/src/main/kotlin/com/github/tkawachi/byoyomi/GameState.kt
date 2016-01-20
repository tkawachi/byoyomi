package com.github.tkawachi.byoyomi

import com.github.tkawachi.byoyomi.CenterButtonState.*

interface GameState {
    /**
     * player がボタンを押した。
     */
    fun buttonPressed(player: Player): GameState

    /**
     * 一時停止ボタンが押された。
     */
    fun pausePressed(): GameState

    /**
     * 再開ボタンが押された。
     */
    fun resumePressed(): GameState

    /**
     * 初期化ボタンが押された。
     */
    fun resetPressed(): GameState

    /**
     * player のタイマーがきれた。
     */
    fun timerExpired(player: Player): GameState

    /**
     * 中央ボタンの状態
     */
    val centerButtonState: CenterButtonState
}

abstract class DefaultGameState(private val game: Game) : GameState {
    override fun buttonPressed(player: Player): GameState = this
    override fun pausePressed(): GameState = this
    override fun timerExpired(player: Player): GameState = this
    override fun resumePressed(): GameState = this
    override fun resetPressed(): GameState {
        game.reset()
        return BeforeStart(game)
    }

    override val centerButtonState: CenterButtonState = ShowPauseButton
}

class BeforeStart(private val game: Game) : DefaultGameState(game) {
    override fun buttonPressed(player: Player): GameState {
        game.startTimer(player.other())
        return PlayerThinking(game, player.other())
    }
    override val centerButtonState: CenterButtonState = ShowInactivePauseButton
}

class PlayerThinking(private val game: Game, private val thinking: Player) :
        DefaultGameState(game) {
    override fun buttonPressed(player: Player): GameState {
        return if (thinking == player) {
            game.startTimer(player.other())
            PlayerThinking(game, player.other())
        } else {
            this
        }
    }

    override fun pausePressed(): GameState {
        game.pause()
        return Paused(game, this, thinking)
    }

    override fun timerExpired(player: Player): GameState = TimeOver(game)
}

class Paused(
        private val game: Game,
        private val beforeState: GameState,
        private val thinking: Player) : DefaultGameState(game) {
    override fun resumePressed(): GameState {
        game.resume(thinking)
        return beforeState
    }
}

class TimeOver(private val game: Game): DefaultGameState(game) {
    override val centerButtonState: CenterButtonState = ShowResetButton
}
