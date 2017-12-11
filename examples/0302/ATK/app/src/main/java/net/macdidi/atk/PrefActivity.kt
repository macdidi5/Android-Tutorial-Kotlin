package net.macdidi.atk

import android.os.Bundle
import android.preference.PreferenceActivity

// 繼承自PreferenceActivity類別
class PrefActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 載入PrefFragment元件
        fragmentManager.beginTransaction().replace(
                android.R.id.content, PrefFragment()).commit()
    }

    // Android 4.4、API level 19加入的方法
    // API level 19以後的版本必須覆寫這個方法，
    //    檢查使用的Fragment是否有效
    override fun isValidFragment(fragmentName: String): Boolean {
        return PrefFragment::class.java.name == fragmentName
    }

}
