package com.github.tkawachi.byoyomi.timer

import android.os.CountDownTimer
import com.github.tkawachi.byoyomi.Clock
import com.github.tkawachi.durationkt.Duration
import com.github.tkawachi.durationkt.millis

open class PauseableTimer (
        private val duration: Duration,
        private val resolutionMillis: Long,
        private val clock: Clock,
        private val onTick: (Duration) -> Unit,
        private val onFinish: () -> Unit) {

    private var restDuration: Duration = duration
    private var startAt: Long? = null
    private var countDownTimer = MyCountDownTimer(restDuration.toMillis())

    fun resume() {
        synchronized(this, {
            if (startAt == null) {
                startAt = clock.getCurrent()
                countDownTimer.start()
            }
        })
    }

    fun pause() {
        synchronized(this, {
            val s = startAt
            if (s != null) {
                countDownTimer.cancel()
                restDuration -= (clock.getCurrent() - s).millis
                countDownTimer = MyCountDownTimer(restDuration.toMillis())
                startAt = null
            }
        })
    }

    fun isResumed(): Boolean = startAt != null

    fun isPaused(): Boolean = !isResumed()


    private inner class MyCountDownTimer(millis: Long): CountDownTimer(millis, resolutionMillis) {
        override fun onFinish() {
            this@PauseableTimer.onFinish()
        }

        override fun onTick(millisUntilFinished: Long) {
            this@PauseableTimer.onTick(millisUntilFinished.millis)
        }

    }
}
