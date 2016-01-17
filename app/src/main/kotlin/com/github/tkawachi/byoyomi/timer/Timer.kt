package com.github.tkawachi.byoyomi.timer

interface Timer {
    /**
     * Start player's turn.
     */
    fun turnStart()

    /**
     * End player's turn.
     */
    fun turnEnd()

    /**
     * Pause timer
     */
    fun pause()

    /**
     * Resume timer
     */
    fun resume()
}
