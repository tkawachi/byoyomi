package com.github.tkawachi.byoyomi

interface GameState {
    /**
     * player がボタンを押した。
     */
    fun buttonPressed(player: Player): GameState

    /**
     * pause ボタンが押された。
     */
    fun pausePressed(): GameState
}

abstract class DefaultGameState : GameState {
    override fun buttonPressed(player: Player): GameState = this
    override fun pausePressed(): GameState = this
}

class BeforeStart(val game: Game) : DefaultGameState() {
    override fun buttonPressed(player: Player): GameState {
        game.startTimer(player.other())
        return PlayerThinking(game, player.other())
    }
}

class PlayerThinking(val game: Game, val thinking: Player) : DefaultGameState() {
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
}

class Paused(val game: Game, val beforeState: GameState, val thinking: Player) : DefaultGameState() {
    override fun pausePressed(): GameState {
        game.resume(thinking)
        return beforeState
    }
}

class TimeOver(val game: Game): DefaultGameState() {
    override fun pausePressed(): GameState {
        game.reset()
        return BeforeStart(game)
    }
}
