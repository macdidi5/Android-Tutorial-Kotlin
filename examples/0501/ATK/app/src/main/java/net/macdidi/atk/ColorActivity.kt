package net.macdidi.atk

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.LinearLayout

class ColorActivity : Activity() {

    private val color_gallery: LinearLayout by bind(R.id.color_gallery)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)

        val listener = ColorListener()

        for (c in Colors.values()) {
            val button = Button(this)
            button.setId(c.parseColor())
            val layout = LinearLayout.LayoutParams(128, 128)
            layout.setMargins(6, 6, 6, 6)
            button.setLayoutParams(layout)
            button.setBackgroundColor(c.parseColor())

            button.setOnClickListener(listener)

            color_gallery.addView(button)
        }
    }

    private inner class ColorListener : View.OnClickListener {

        override fun onClick(view: View) {
            val action = this@ColorActivity.intent.action

            // 經由設定元件啟動
            if (action != null && action == "net.macdidi.atk.CHOOSE_COLOR") {
                // 建立SharedPreferences物件
                val editor = PreferenceManager.getDefaultSharedPreferences(
                        this@ColorActivity).edit()
                // 儲存預設顏色
                editor.putInt("DEFAULT_COLOR", view.id)
                // 寫入設定值
                editor.commit()
            }
            // 經由新增或修改記事的元件啟動
            else {
                val result = intent
                result.putExtra("colorId", view.id)
                setResult(Activity.RESULT_OK, result)
            }

            finish()
        }

    }
}
