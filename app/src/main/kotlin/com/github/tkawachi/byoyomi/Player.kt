package com.github.tkawachi.byoyomi

enum class Player {
    Player1, Player2
}

fun Player.other(): Player = when(this) {
    Player.Player1 -> Player.Player2
    Player.Player2 -> Player.Player1
}
