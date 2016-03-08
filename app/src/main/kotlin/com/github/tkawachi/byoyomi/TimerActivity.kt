package com.github.tkawachi.byoyomi

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.github.tkawachi.byoyomi.Player.Player1
import com.github.tkawachi.byoyomi.Player.Player2
import com.github.tkawachi.byoyomi.sound.Sound
import com.github.tkawachi.byoyomi.sound.Speech
import com.github.tkawachi.byoyomi.timer.ByobomiTimer
import com.github.tkawachi.byoyomi.timer.Timer
import com.github.tkawachi.byoyomi.timer.TimerState
import com.github.tkawachi.durationkt.Duration
import com.github.tkawachi.durationkt.seconds
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import org.jetbrains.anko.info
import org.jetbrains.anko.verbose
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class TimerActivity : FragmentActivity(), Game, AnkoLogger {

    private val setting = Setting(Duration.zero, 30.seconds) // TODO
    private val clock = SystemClock()

    private var _state: GameState = BeforeStart(this)
    private var state: GameState
        get() = _state
        set(value) {
            applyViewToState(value)
            _state = value
        }
    private var buttons: TurnButtonPair? = null
    private var sound: Sound? = null
    private var pauseOverlay: PauseOverlayFragment? = null
    private val timers: MutableMap<Player, Timer> = HashMap()

    private fun initTimer(setting: Setting, player: Player): Timer {
        val playerString = "$player"
        buttons?.byPlayer(player)?.text = TimerState.fromSetting(setting).display()

        return ByobomiTimer(clock, setting, 100L,
                { state ->
                    verbose("$playerString ${state.display()}")

                    val 消費秒 = (setting.秒読み時間 - state.残り秒読み時間).toSeconds().toInt()

                    buttons?.byPlayer(player)?.text = state.display()

                    if (state.残り持ち時間 == Duration.zero && state.残り秒読み時間 < 10.seconds) {
                        val n = 10 - state.残り秒読み時間.toSeconds().toInt()
                        sound?.playNumber(n)
                    } else if (消費秒 > 0 && 消費秒 % 10 == 0) {
                        sound?.playSecond(消費秒)
                    }
                },
                {
                    sound?.playByoyomiStart(it.toSeconds())
                },
                {
                    state = state.timerExpired(player)
                    verbose("timeOver $playerString")
                }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_timer)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.makeFullscreen()

        find<Button>(R.id.toSettingsBtn).setOnClickListener { info("clicked") } // TODO
        find<View>(R.id.player2Btn).rotation = 180f

        fun getButton(player: Player): TurnButton {
            val id = when (player) {
                Player1 -> R.id.player1Btn
                Player2 -> R.id.player2Btn
            }
            val b = find<TurnButton>(id)
            b.setOnTouchDown(false, { state = state.buttonPressed(player) })
            return b
        }
        buttons = TurnButtonPair(getButton(Player1), getButton(Player2))

        find<Button>(R.id.pauseBtn).setOnClickListener { state = state.pausePressed() }
        find<Button>(R.id.resetBtn).setOnClickListener { state = state.resetPressed() }

        val fm = supportFragmentManager
        pauseOverlay = fm.findFragmentById(R.id.pauseOverlay) as PauseOverlayFragment
        pauseOverlay?.resumeBtn?.setOnClickListener { state = state.resumePressed() }
        pauseOverlay?.resetBtn?.setOnClickListener { state = state.resetPressed() }

        sound = Speech(this)

        reset()
        applyViewToState(state)
    }

    override fun pause() {
        timers.forEach { it.value.pause() }
        showPauseOverlay()
    }

    override fun resume(resumedPlayer: Player) {
        hidePauseOverlay()
        timers.get(resumedPlayer)?.resume()
    }

    override fun reset() {
        buttons?.setDefault()
        timers.put(Player1, initTimer(setting, Player1))?.pause()
        timers.put(Player2, initTimer(setting, Player2))?.pause()
        hidePauseOverlay()
    }

    override fun startTimer(startingPlayer: Player) {
        timers.get(startingPlayer.other())?.turnEnd()
        timers.get(startingPlayer)?.turnStart()
        sound?.playTurnStart()
        buttons?.startTurn(startingPlayer)
    }

    private fun hidePauseOverlay() {
        val fm = supportFragmentManager
        fm.beginTransaction()
                .hide(pauseOverlay)
                .commit()
    }

    private fun showPauseOverlay() {
        val fm = supportFragmentManager
        fm.beginTransaction()
                .show(pauseOverlay)
                .commit()
    }

    private fun applyViewToState(state: GameState) {
        showCenterButton(state.centerButtonState)
    }

    private fun showCenterButton(state: CenterButtonState) {
        val pauseBtn = find<Button>(R.id.pauseBtn)
        val resetBtn = find<Button>(R.id.resetBtn)
        when (state) {
            CenterButtonState.ShowPauseButton -> {
                resetBtn.visibility = View.INVISIBLE
                pauseBtn.visibility = View.VISIBLE
                pauseBtn.isEnabled = true
            }
            CenterButtonState.ShowInactivePauseButton -> {
                resetBtn.visibility = View.INVISIBLE
                pauseBtn.visibility = View.VISIBLE
                pauseBtn.isEnabled = false
            }
            CenterButtonState.ShowResetButton -> {
                resetBtn.visibility = View.VISIBLE
                pauseBtn.visibility = View.INVISIBLE
            }
        }
    }

}
