package com.github.tkawachi.byoyomi.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.verbose
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class Speech(context: Context, val toneType: Int, val toneDurationMs: Int) :
        TextToSpeech.OnInitListener, AnkoLogger, Sound {

    constructor(context: Context): this(context, ToneGenerator.TONE_CDMA_PRESSHOLDKEY_LITE, 200)

    private var tts = TextToSpeech(context, this)
    private var isInitialized = false
    private var nextUtteranceId = AtomicInteger(1)
    private val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    private var volume: Int = 0
    private var _toneG: ToneGenerator? = null
    private var toneG: ToneGenerator?
        get() = _toneG
        set(value) {
            _toneG?.release()
            _toneG = value
        }

    override fun playTurnStart() {
        updateVolume()
        toneG?.startTone(toneType, toneDurationMs)
    }

    override fun playSecond(second: Int) = speak("${second}秒")

    override fun playNumber(n: Int) = speak("$n")

    override fun playByoyomiStart(seconds: Long) = speak("ここから一手 $seconds 秒です。")

    private fun getToneVolume(): Int {
        val volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        return ((volume.toDouble() / maxVolume) * 100).toInt()
    }

    private fun updateVolume() {
        val newVolume = getToneVolume()
        if (volume != newVolume) {
            volume = newVolume
            toneG = ToneGenerator(AudioManager.STREAM_ALARM, volume)
            debug("volume: $volume, maxVolume: $maxVolume, toneVolume: $volume")
        }
    }

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
