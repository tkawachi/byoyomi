package com.github.tkawachi.byoyomi.timer

import com.github.tkawachi.byoyomi.Clock
import com.github.tkawachi.byoyomi.Setting
import com.github.tkawachi.durationkt.Duration
import com.github.tkawachi.durationkt.seconds
import org.jetbrains.anko.AnkoLogger

class ByobomiTimer(
        private val clock: Clock,
        private val setting: Setting,
        private val countDownInterval: Long,
        private val displayTime: (TimerState) -> Unit,
        private val startByoyomi: (Duration) -> Unit,
        private val timeOver: () -> Unit
) : Timer, AnkoLogger {

    private var countDownTimer: PauseableTimer =
            if (setting.持ち時間 > 0.seconds) MotiJikanTimer(setting.持ち時間)
            else ByoyomiTimer(setting.秒読み時間)

    init {
        displayTime(TimerState.fromSetting(setting))
    }

    override fun turnEnd() {
        when (countDownTimer) {
            is MotiJikanTimer -> {
                countDownTimer.pause()
            }
            is ByoyomiTimer -> {
                countDownTimer.pause()
                countDownTimer = ByoyomiTimer(setting.秒読み時間)
                displayTime(TimerState(Duration.zero, setting.秒読み時間))
            }
            else -> {
                error("Unexpected $countDownTimer")
            }
        }
    }

    override fun turnStart() {
        countDownTimer.resume()
    }

    override fun pause() {
        countDownTimer.pause()
    }

    override fun resume() {
        countDownTimer.resume()
    }

    private inner class MotiJikanTimer(duration: Duration) :
            PauseableTimer(duration, countDownInterval, clock, { rest ->
                displayTime(TimerState(rest, setting.秒読み時間))
            }, {
                countDownTimer = ByoyomiTimer(setting.秒読み時間)
                countDownTimer.resume()
                startByoyomi(setting.秒読み時間)
            })

    private inner class ByoyomiTimer(duration: Duration) :
            PauseableTimer(duration, countDownInterval, clock, { rest ->
                displayTime(TimerState(Duration.zero, rest))
            }, {
                displayTime(TimerState.zero)
                timeOver()
            })


}
