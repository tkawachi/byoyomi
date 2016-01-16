package net.pikot.byoyomi

class SystemClock: Clock {
    override fun getCurrent(): Long {
        return System.currentTimeMillis()
    }
}
