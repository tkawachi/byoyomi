package com.github.tkawachi.byoyomi.sound

import com.github.tkawachi.byoyomi.timer.TimerState

sealed class PlayState {

    class Init : PlayState() {
        override fun toString(): String = "Init"
        override fun equals(other: Any?): Boolean =
            when (other) {
                is Init -> true
                else -> false
            }

        override fun hashCode(): Int{
            return 0
        }
    }

    class Played(val lastState: TimerState): PlayState() {
        override fun toString(): String = "Played($lastState)"

        override fun equals(other: Any?): Boolean =
            when(other) {
                is Played -> lastState == other.lastState
                else -> false
            }

        override fun hashCode(): Int{
            return lastState.hashCode()
        }
    }
}
