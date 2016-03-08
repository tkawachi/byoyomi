package com.github.tkawachi.byoyomi.sound

import com.github.tkawachi.byoyomi.Setting
import com.github.tkawachi.byoyomi.timer.TimerState
import com.github.tkawachi.durationkt.Duration
import com.github.tkawachi.durationkt.millis
import com.github.tkawachi.durationkt.seconds
import junit.framework.TestCase

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PlayStateUpdaterTest {

    @Test
    fun testStart() {
        val setting = Setting(Duration.zero, 30.seconds)
        val updater = PlayStateUpdater(setting, 200.millis)
        val state = TimerState.fromSetting(setting)
        val actual = updater.update(state, PlayState.Init())
        val expected = Pair(
                PlayState.Played(state),
                PlayOp.Start(setting.秒読み時間.toSeconds())
        )

        assertEquals(expected, actual)
    }

}
