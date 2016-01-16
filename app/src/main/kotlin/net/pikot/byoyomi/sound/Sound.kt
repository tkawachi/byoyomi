package net.pikot.byoyomi.sound

interface Sound {
    fun playTurnStart()

    fun playSecond(second: Int)

    fun playNumber(n: Int)

    fun playByoyomiStart(seconds: Int)
}
