package net.macdidi.atk

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText

class ItemActivity : AppCompatActivity() {

    private val title_text : EditText by bind(R.id.title_text)
    private val content_text : EditText by bind(R.id.content_text)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        // 如果是修改記事
        if (intent.action == "net.macdidi.atk.EDIT_ITEM") {
            // 接收與設定記事標題
            val titleText = intent.getStringExtra("titleText")
            title_text.setText(titleText)
        }
    }

    // 點擊確定與取消按鈕都會呼叫這個方法
    fun onSubmit(view: View) {
        // 確定按鈕
        if (view.id == R.id.ok_item) {
            // 讀取使用者輸入的標題與內容
            val titleText = title_text.text.toString()
            val contentText = content_text.text.toString()

            // 設定標題與內容
            intent.putExtra("titleText", titleText)
            intent.putExtra("contentText", contentText)

            // 設定回應結果為確定
            setResult(Activity.RESULT_OK, intent)
        } else {
            // 設定回應結果為取消
            setResult(Activity.RESULT_CANCELED, intent)
        }

        // 結束
        finish()
    }

    // 以後需要擴充的功能
    fun clickFunction(view: View) {
        when (view.id) {
            R.id.take_picture -> {
            }
            R.id.record_sound -> {
            }
            R.id.set_location -> {
            }
            R.id.set_alarm -> {
            }
            R.id.select_color -> {
            }
        }
    }

}
