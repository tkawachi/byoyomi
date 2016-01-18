package com.github.tkawachi.byoyomi

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.github.tkawachi.byoyomi.Player.Player1
import com.github.tkawachi.byoyomi.Player.Player2
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
class TimerActivity : Activity(), Game, AnkoLogger {

    private val setting = Setting(0, 30) // TODO
    private val clock = SystemClock()

    private var state: GameState = BeforeStart(this)
    private var buttons: TurnButtonPair? = null
    private var sound: Sound? = null
    private var player1Timer: Timer? = null
    private var player2Timer: Timer? = null

    private fun initTimer(setting: Setting, player: Player): Timer {
        val playerString = "$player"
        buttons?.byPlayer(player)?.text = DisplayData.fromSetting(setting).buttonText()

        return ByobomiTimer(clock, setting, 100L,
                { data ->
                    verbose("$playerString ${data.buttonText()}")
                    buttons?.byPlayer(player)?.text = data.buttonText()
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
                    state = TimeOver(this)
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
            b.setOnTouchDown { state = state.buttonPressed(player) }
            return b
        }
        buttons = TurnButtonPair(getButton(Player1), getButton(Player2))

        find<Button>(R.id.pauseBtn).setOnClickListener {
            state = state.pausePressed()
        }
        sound = Speech(this)
        reset()
    }

    override fun pause() {
        player1Timer?.pause()
        player2Timer?.pause()
    }

    override fun resume(resumedPlayer: Player) {
        timerByPlayer(resumedPlayer)?.resume()
    }

    override fun reset() {
        player1Timer?.pause()
        player2Timer?.pause()

        state = BeforeStart(this)

        player1Timer = initTimer(setting, Player1)
        player2Timer = initTimer(setting, Player2)
        buttons?.setDefault()
    }

    private fun timerByPlayer(player: Player): Timer? = when (player) {
        Player1 -> player1Timer
        Player2 -> player2Timer
    }

    override fun startTimer(startingPlayer: Player) {
        timerByPlayer(startingPlayer.other())?.turnEnd()
        timerByPlayer(startingPlayer)?.turnStart()
        sound?.playTurnStart()
        buttons?.startTurn(startingPlayer)
    }

}
