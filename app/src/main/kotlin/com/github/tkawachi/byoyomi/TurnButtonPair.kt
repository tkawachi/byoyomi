package com.github.tkawachi.byoyomi

class TurnButtonPair(val button1: TurnButton, val button2: TurnButton) {
    fun startTurn(player: Player) {
        when(player) {
            Player.Player1 -> {
                button1.state = TurnButton.State.MyTurn
                button2.state = TurnButton.State.OthersTurn
            }
            Player.Player2 -> {
                button1.state = TurnButton.State.OthersTurn
                button2.state = TurnButton.State.MyTurn
            }
        }
    }

    fun byPlayer(player: Player): TurnButton = when (player) {
        Player.Player1 -> button1
        Player.Player2 -> button2
    }
}
