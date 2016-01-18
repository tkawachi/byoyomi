package com.github.tkawachi.byoyomi

interface Game {
    /**
     * player のタイマーを開始する。
     */
    fun startTimer(startingPlayer: Player)

    /**
     * ゲームを一時停止する。
     */
    fun pause()

    /**
     * ゲームを一時停止から再開する。
     */
    fun resume(resumedPlayer: Player)

    /**
     * 初期状態に戻す。
     */
    fun reset()
}
