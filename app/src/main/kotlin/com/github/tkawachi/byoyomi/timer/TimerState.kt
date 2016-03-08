package com.github.tkawachi.byoyomi.timer

import com.github.tkawachi.byoyomi.Setting
import com.github.tkawachi.durationkt.Duration

data class TimerState(val 残り持ち時間: Duration, val 残り秒読み時間: Duration) {

    companion object {
        val zero = TimerState(Duration.zero, Duration.zero)
        fun fromSetting(setting: Setting): TimerState = TimerState(setting.持ち時間, setting.秒読み時間)
    }
}
