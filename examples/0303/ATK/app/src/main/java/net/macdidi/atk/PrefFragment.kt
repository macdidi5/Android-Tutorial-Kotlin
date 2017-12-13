package net.macdidi.atk

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager

class PrefFragment : PreferenceFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    private val defaultColor: Preference
            by lazy { findPreference("DEFAULT_COLOR") }
    private val sharedPreferences: SharedPreferences
            by lazy { PreferenceManager.getDefaultSharedPreferences(this.activity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 指定使用的設定畫面配置資源
        addPreferencesFromResource(R.xml.mypreference)
    }

    override fun onSharedPreferenceChanged(sharedPreference: SharedPreferences?,
                                           key: String?) {
        if (key == "DEFAULT_COLOR") {
            setColorSummary()
        }
    }

    override fun onResume() {
        super.onResume()

        setColorSummary()
    }

    private fun setColorSummary() {
        // 讀取設定的預設顏色
        val color = sharedPreferences.getInt("DEFAULT_COLOR", -1)

        if (color != -1) {
            // 設定顏色說明
            defaultColor.summary = getString(R.string.default_color_summary) +
                    ": " + ItemActivity.getColors(color)
        }
    }

}
