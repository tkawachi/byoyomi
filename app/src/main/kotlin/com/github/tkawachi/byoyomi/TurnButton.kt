package com.github.tkawachi.byoyomi

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.Button

class TurnButton(context: Context?, attrs: AttributeSet?) :
        Button(context, attrs) {

    private var _state: State = State.Default
    var state: State
        get() = _state
        set(value) {
            _state = value
            applyStyle(_state)
        }

    init {
        applyStyle(state)
    }

    private fun applyStyle(state: State) {
        setBackgroundColor(state.backgroundColor)
        setTextColor(state.textColor)
    }

    enum class State(val backgroundColor: Int, val textColor: Int) {
        Default(Color.parseColor("#90A7FF"), Color.BLACK),
        MyTurn(Color.parseColor("#436BFF"), Color.WHITE),
        OthersTurn(Color.parseColor("#48547F"), Color.parseColor("#aaaaaa"))
    }
}

