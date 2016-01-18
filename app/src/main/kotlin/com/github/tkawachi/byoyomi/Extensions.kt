package com.github.tkawachi.byoyomi

import android.view.MotionEvent
import android.view.View
import android.view.Window


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

fun Window.makeFullscreen() {
    // Note that some of these constants are new as of API 16 (Jelly Bean)
    // and API 19 (KitKat). It is safe to use them, as they are inlined
    // at compile-time and do nothing on earlier devices.
    val visibilityFlag = View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

    this.decorView.systemUiVisibility = visibilityFlag
    this.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
        // フルスクリーンでない時はフルスクリーンにする
        if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
            this.decorView.systemUiVisibility = visibilityFlag
        }
    }
}
