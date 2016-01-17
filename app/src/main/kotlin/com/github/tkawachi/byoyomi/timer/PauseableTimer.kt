package com.github.tkawachi.byoyomi.timer

import android.os.CountDownTimer
import com.github.tkawachi.byoyomi.Clock

open class PauseableTimer (
        private val millis: Long,
        private val resolutionMillis: Long,
        private val clock: Clock,
        private val onTick: (Long) -> Unit,
        private val onFinish: () -> Unit) {

    private var restMillis: Long = millis
    private var startAt: Long? = null
    private var countDownTimer = MyCountDownTimer(restMillis)

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
                restMillis -= clock.getCurrent() - s
                countDownTimer = MyCountDownTimer(restMillis)
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
            this@PauseableTimer.onTick(millisUntilFinished)
        }

    }
}
