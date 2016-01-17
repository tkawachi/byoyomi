package com.github.tkawachi.byoyomi

data class DisplayData(val 残り持ち時間: Int, val 残り秒読み時間: Int) {
    fun buttonText(): String {
        if (残り持ち時間 > 0) {
            val minutes = 残り持ち時間 / 60
            val seconds = 残り持ち時間 % 60
            return "%02d:%02d".format(minutes, seconds)
        } else if (残り秒読み時間 > 0) {
            return 残り秒読み時間.toString()
        } else {
            return "Time Over"
        }
    }

    companion object {
        fun fromSetting(setting: Setting): DisplayData = DisplayData(setting.持ち時間, setting.秒読み時間)
    }
}
