package com.github.tkawachi.byoyomi

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.github.tkawachi.byoyomi.sound.Sound
import com.github.tkawachi.byoyomi.sound.Speech
import com.github.tkawachi.byoyomi.timer.ByobomiTimer
import com.github.tkawachi.byoyomi.timer.Timer
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.info
import org.jetbrains.anko.verbose

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class TimerActivity : Activity(), AnkoLogger {

    private val setting = Setting(0, 30) // TODO

    private val clock = SystemClock()

    private var state = GameState.NotStarted
    private var pausedState: GameState? = null
    private var player1Btn: Button? = null
    private var player2Btn: Button? = null
    private var sound: Sound? = null
    private var player1Timer: Timer? = null
    private var player2Timer: Timer? = null


    // Note that some of these constants are new as of API 16 (Jelly Bean)
    // and API 19 (KitKat). It is safe to use them, as they are inlined
    // at compile-time and do nothing on earlier devices.
    private val visibilityFlag = View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

    private fun initTimer(setting: Setting, player1: Boolean): Timer {
        val playerString = "player${if (player1) 1 else 2}"
        (if (player1) player1Btn else player2Btn)?.text = DisplayData.fromSetting(setting).buttonText()

        return ByobomiTimer(clock, setting, 100L,
                { data ->
                    verbose("$playerString ${data.buttonText()}")
                    val btn = if (player1) player1Btn else player2Btn
                    btn?.text = data.buttonText()
                    if (data.残り持ち時間 == 0 && data.残り秒読み時間 < 10) {
                        val n = 10 - data.残り秒読み時間
                        sound?.playNumber(n)
                    } else if (setting.秒読み時間 > data.残り秒読み時間 &&
                            (setting.秒読み時間 - data.残り秒読み時間) % 10 == 0) {
                        sound?.playSecond(setting.秒読み時間 - data.残り秒読み時間)
                    }
                },
                {
                    sound?.playByoyomiStart(it)
                },
                {
                    state = GameState.TimeOver
                    verbose("timeOver $playerString")
                }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_timer)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        window.decorView.systemUiVisibility = visibilityFlag
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // フルスクリーンでない時はフルスクリーンにする
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                window.decorView.systemUiVisibility = visibilityFlag
            }
        }


        find<Button>(R.id.toSettingsBtn).setOnClickListener { info("clicked") } // TODO
        find<View>(R.id.player2Btn).rotation = 180f

        player1Btn = find<Button>(R.id.player1Btn)
        player1Btn?.setOnTouchDown { startTimer(true) }
        player2Btn = find<Button>(R.id.player2Btn)
        player2Btn?.setOnTouchDown { startTimer(false) }

        find<Button>(R.id.pauseBtn).setOnClickListener {
            when (state) {
                GameState.Player1Thinking -> {
                    pausedState = state
                    state = GameState.Pause
                    player1Timer!!.pause()
                }
                GameState.Player2Thinking -> {
                    pausedState = state
                    state = GameState.Pause
                    player2Timer!!.pause()
                }
                GameState.Pause -> {
                    state = pausedState!!
                    when (state) {
                        GameState.Player1Thinking -> player1Timer!!.resume()
                        GameState.Player2Thinking -> player2Timer!!.resume()
                        else -> {
                        }
                    }
                }
                GameState.TimeOver -> {
                    state = GameState.NotStarted
                    player1Timer = initTimer(setting, true)
                    player2Timer = initTimer(setting, false)
                }
                else -> {
                }
            }
        }
        player1Timer = initTimer(setting, true)
        player2Timer = initTimer(setting, false)
        sound = Speech(this)
    }

    fun startTimer(player1: Boolean) {
        when (state) {
            GameState.NotStarted -> {
                sound?.playTurnStart()
                if (player1) {
                    player2Timer!!.turnStart()
                    state = GameState.Player2Thinking
                } else {
                    player1Timer!!.turnStart()
                    state = GameState.Player1Thinking
                }
            }
            GameState.Player1Thinking -> {
                if (player1) {
                    sound?.playTurnStart()
                    player1Timer!!.turnEnd()
                    player2Timer!!.turnStart()
                    state = GameState.Player2Thinking
                }
            }
            GameState.Player2Thinking -> {
                if (!player1) {
                    sound?.playTurnStart()
                    player2Timer!!.turnEnd()
                    player1Timer!!.turnStart()
                    state = GameState.Player1Thinking
                }
            }
            else -> {

            }
        }
    }

}
