package com.github.tkawachi.byoyomi.sound

sealed class PlayOp {
    class Number(val n: Long) : PlayOp() {
        override fun toString(): String = "Number($n)"

        override fun equals(other: Any?): Boolean =
                when (other) {
                    is Number -> n == other.n
                    else -> false
                }

        override fun hashCode(): Int {
            return n.hashCode()
        }

    }

    class Byou(val n: Long) : PlayOp() {
        override fun toString(): String = "Byou($n)"

        override fun equals(other: Any?): Boolean =
                when (other) {
                    is Byou -> n == other.n
                    else -> false
                }

        override fun hashCode(): Int {
            return n.hashCode()
        }
    }

    class Start(val n: Long) : PlayOp() {
        override fun toString(): String = "Start($n)"

        override fun equals(other: Any?): Boolean =
                when(other) {
                    is Start -> n == other.n
                    else -> false
                }

        override fun hashCode(): Int{
            return n.hashCode()
        }
    }
}
