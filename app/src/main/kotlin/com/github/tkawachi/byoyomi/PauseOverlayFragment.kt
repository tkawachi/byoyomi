package com.github.tkawachi.byoyomi

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find

class PauseOverlayFragment : Fragment(), AnkoLogger {

    var resumeBtn: Button? = null
    var resetBtn: Button? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.pause_overlay, container, false)
        resumeBtn = view?.find<Button>(R.id.resumeBtn)
        resetBtn = view?.find<Button>(R.id.overlayResetBtn)
        return view
    }
}
