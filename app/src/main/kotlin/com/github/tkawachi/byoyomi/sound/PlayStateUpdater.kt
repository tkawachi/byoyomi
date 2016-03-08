package com.github.tkawachi.byoyomi.sound

import com.github.tkawachi.byoyomi.Setting
import com.github.tkawachi.byoyomi.timer.TimerState
import com.github.tkawachi.durationkt.Duration

class PlayStateUpdater(val setting: Setting, playAhead: Duration) {

    fun update(currentState: TimerState, state: PlayState): Pair<PlayState, PlayOp?> {
        when (state) {
            is PlayState.Init ->
                return Pair(PlayState.Played(currentState), PlayOp.Start(setting.秒読み時間.toSeconds()))
            is PlayState.Played -> {
                state.lastState
                return Pair(state, null)
            }
        }
    }

}
