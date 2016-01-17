package com.github.tkawachi.byoyomi

import android.view.MotionEvent
import android.view.View


fun View.setOnTouchDown(f: () -> Unit) {
    this.setOnTouchListener { view, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            f()
            true
        } else {
            false
        }
    }
}
