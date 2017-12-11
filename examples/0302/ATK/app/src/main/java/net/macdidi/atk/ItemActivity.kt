package net.macdidi.atk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TableLayout
import java.util.*

class ItemActivity : AppCompatActivity() {

    private val title_text : EditText by bind(R.id.title_text)
    private val content_text : EditText by bind(R.id.content_text)

    // 啟動功能用的請求代碼
    enum class ItemAction {
        CAMERA, RECORD, LOCATION, ALARM, COLOR
    }

    // 記事物件
    private var item : Item = Item()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        // 讀取Action名稱
        val action = intent.action

        // 如果是修改記事
        if (action == "net.macdidi.atk.EDIT_ITEM") {
            // 接收記事物件與設定標題、內容
            item = intent.extras.getSerializable(
                    "net.macdidi.atk.Item") as Item
            title_text.setText(item.title)
            content_text.setText(item.content)

            // 根據記事物件的顏色設定畫面的背景顏色
            findViewById<TableLayout>(R.id.item_container)
                    .setBackgroundColor(item.color.parseColor())
        }
    }

    // 更改參數data的型態為Intent?
    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {

            val actionRequest = ItemAction.values()[requestCode]

            when (actionRequest) {
                ItemAction.CAMERA -> {
                }
                ItemAction.RECORD -> {
                }
                ItemAction.LOCATION -> {
                }
                ItemAction.ALARM -> {
                }
                // 設定顏色
                ItemAction.COLOR -> {
                    if (data != null) {
                        val colorId = data.getIntExtra(
                                "colorId", Colors.LIGHTGREY.parseColor())
                        item.color = getColors(colorId)
                        // 根據選擇的顏色設定畫面的背景顏色
                        findViewById<TableLayout>(R.id.item_container)
                                .setBackgroundColor(item.color.parseColor())
                    }
                }
            }
        }
    }

    // 點擊確定與取消按鈕都會呼叫這個函式
    fun onSubmit(view: View) {
        // 確定按鈕
        if (view.id == R.id.ok_item) {
            // 讀取使用者輸入的標題與內容
            val titleText = title_text.text.toString()
            val contentText = content_text.text.toString()

            // 設定記事物件的標題與內容
            item.title = titleText
            item.content = contentText

            // 如果是修改記事
            if (intent.action == "net.macdidi.atk.EDIT_ITEM") {
                item.lastModify = Date().time
            }
            // 新增記事
            else {
                item.datetime = Date().time

                // 建立SharedPreferences物件
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                // 讀取設定的預設顏色
                val color = sharedPreferences.getInt("DEFAULT_COLOR", -1)
                item.color = getColors(color)
            }

            // 設定回傳的記事物件
            intent.putExtra("net.macdidi.atk.Item", item)
            setResult(Activity.RESULT_OK, intent)
        } else {
            // 設定回應結果為取消
            setResult(Activity.RESULT_CANCELED, intent)
        }

        // 結束
        finish()
    }

    // 使用者選擇返回鍵
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 設定回應結果為取消
            setResult(Activity.RESULT_CANCELED, intent)
        }

        return super.onKeyDown(keyCode, event)
    }

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
            //選擇設定顏色功能
            R.id.select_color -> {
                // 啟動設定顏色的Activity元件
                startActivityForResult(Intent(this, ColorActivity::class.java),
                        ItemAction.COLOR.ordinal)
            }
        }
    }

    // 可以使用類別名稱呼叫這個方法
    companion object {
        // 轉換顏色值為Colors型態
        public fun getColors(color: Int): Colors {
            var result = Colors.LIGHTGREY

            if (color == Colors.BLUE.parseColor()) {
                result = Colors.BLUE
            } else if (color == Colors.PURPLE.parseColor()) {
                result = Colors.PURPLE
            } else if (color == Colors.GREEN.parseColor()) {
                result = Colors.GREEN
            } else if (color == Colors.ORANGE.parseColor()) {
                result = Colors.ORANGE
            } else if (color == Colors.RED.parseColor()) {
                result = Colors.RED
            }

            return result
        }
    }

}
