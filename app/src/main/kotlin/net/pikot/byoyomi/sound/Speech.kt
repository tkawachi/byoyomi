package net.pikot.byoyomi.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.verbose
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class Speech(context: Context) : TextToSpeech.OnInitListener, AnkoLogger, Sound {

    private var tts = TextToSpeech(context, this)
    private var isInitialized = false
    private var nextUtteranceId = AtomicInteger(1)
    private val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    override fun playTurnStart() {
        val volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        val toneVolume = ((volume.toDouble() / maxVolume) * 100).toInt()
        debug("volume: $volume, maxVolume: $maxVolume, toneVolume: $toneVolume")
        val toneG = ToneGenerator(AudioManager.STREAM_ALARM, toneVolume)
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
    }

    override fun playSecond(second: Int) = speak("${second}秒")

    override fun playNumber(n: Int) = speak("$n")

    override fun playByoyomiStart(seconds: Int) = speak("ここから一手 $seconds 秒です。")


    private fun speak(text: String) {
        if (isInitialized) {
            val utteranceId = nextUtteranceId.andIncrement
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "$utteranceId")
        }
    }

    override fun onInit(status: Int) {
        verbose("onInit")
        when (status) {
            TextToSpeech.SUCCESS -> {
                if (tts.isLanguageAvailable(Locale.JAPANESE) >= TextToSpeech.LANG_AVAILABLE) {
                    tts.setLanguage(Locale.JAPANESE)
                    isInitialized = true
                }
            }
            TextToSpeech.ERROR -> {
                error("TTS not available")
            }
        }
    }
}
