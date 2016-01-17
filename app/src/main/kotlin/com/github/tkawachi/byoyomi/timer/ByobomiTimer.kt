package com.github.tkawachi.byoyomi.timer

import com.github.tkawachi.byoyomi.Clock
import com.github.tkawachi.byoyomi.DisplayData
import com.github.tkawachi.byoyomi.Setting
import org.jetbrains.anko.AnkoLogger

class ByobomiTimer(
        private val clock: Clock,
        private val setting: Setting,
        private val countDownInterval: Long,
        private val displayTime: (DisplayData) -> Unit,
        private val startByoyomi: (Int) -> Unit,
        private val timeOver: () -> Unit
) : Timer, AnkoLogger {

    private var countDownTimer: PauseableTimer =
            if (setting.持ち時間 > 0) MotiJikanTimer(setting.持ち時間 * 1000L)
            else ByoyomiTimer(setting.秒読み時間 * 1000L)

    private var lastDisplayData: DisplayData = DisplayData.fromSetting(setting)

    init {
        displayTime(lastDisplayData)
    }

    override fun turnEnd() {
        when (countDownTimer) {
            is MotiJikanTimer -> {
                countDownTimer.pause()
            }
            is ByoyomiTimer -> {
                countDownTimer.pause()
                countDownTimer = ByoyomiTimer((setting.秒読み時間) * 1000L)
                displayTimeIfNecessary(DisplayData(0, setting.秒読み時間))
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

    private fun displayTimeIfNecessary(data: DisplayData) {
        if (lastDisplayData != data) {
            displayTime(data);
            lastDisplayData = data;
        }
    }

    companion object {
        fun restSec(msec: Long): Int = Math.ceil(msec / 1000.0).toInt()
    }

    private inner class MotiJikanTimer(millis: Long) :
            PauseableTimer(millis, countDownInterval, clock, { rest ->
                displayTimeIfNecessary(DisplayData(restSec(rest), setting.秒読み時間))
            }, {
                countDownTimer = ByoyomiTimer(setting.秒読み時間 * 1000L)
                countDownTimer.resume()
                startByoyomi(setting.秒読み時間)
            })

    private inner class ByoyomiTimer(millis: Long) :
            PauseableTimer(millis, countDownInterval, clock, { rest ->
                displayTimeIfNecessary(DisplayData(0, restSec(rest)))
            }, {
                displayTimeIfNecessary(DisplayData(0, 0))
                timeOver()
            })


}
