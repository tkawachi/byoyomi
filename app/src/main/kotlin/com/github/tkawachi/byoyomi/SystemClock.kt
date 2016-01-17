package com.github.tkawachi.byoyomi

class SystemClock: Clock {
    override fun getCurrent(): Long {
        return System.currentTimeMillis()
    }
}
