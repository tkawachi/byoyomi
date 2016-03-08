package com.github.tkawachi.byoyomi

import com.github.tkawachi.byoyomi.timer.TimerState
import com.github.tkawachi.durationkt.Duration

fun TimerState.display(): String {
    if (残り持ち時間 > Duration.zero) {
        val minutes = 残り持ち時間.toMinutes()
        val seconds = 残り持ち時間.toSeconds() % 60
        return "%02d:%02d".format(minutes, seconds)
    } else if (残り秒読み時間 > Duration.zero) {
        return 残り秒読み時間.toSeconds().toString()
    } else {
        return "Time Over"
    }
}
