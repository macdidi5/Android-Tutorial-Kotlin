package net.macdidi.atk

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.Toast
import java.io.File
import java.util.*

class ItemActivity : AppCompatActivity() {

    private val title_text : EditText by bind(R.id.title_text)
    private val content_text : EditText by bind(R.id.content_text)

    // 照片檔案名稱
    private var pictureFileName: String? = null
    // 照片元件
    private val picture: ImageView by bind(R.id.picture)
    // 寫入外部儲存設備授權請求代碼
    private val REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 100

    // 錄音設備授權請求代碼
    private val REQUEST_RECORD_AUDIO_PERMISSION = 101
    // 錄音檔案名稱
    private var recFileName: String? = null

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

    override fun onResume() {
        super.onResume()

        // 如果有照片檔案名稱
        if (!item.fileName.isNullOrEmpty()) {
            // 照片檔案物件
            val file = getFileName("P", ".jpg")

            // 如果照片檔案存在
            if (file.exists()) {
                // 顯示照片元件
                picture.visibility = View.VISIBLE
                // 設定照片
                fileToImageView(file.absolutePath, picture)
            }
        }
    }

    // 更改參數data的型態為Intent?
    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val actionRequest = ItemAction.values()[requestCode]

            when (actionRequest) {
                // 照像
                ItemAction.CAMERA -> {
                    // 設定照片檔案名稱
                    item.fileName = pictureFileName
                }
                ItemAction.RECORD -> {
                    // 設定錄音檔案名稱
                    item.recFileName = recFileName
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
                // 讀取與處理寫入外部儲存設備授權請求
                requestStoragePermission()
            }
            R.id.record_sound -> {
                // 讀取與處理錄音設備授權請求
                requestRecordPermission()
            }
            R.id.set_location -> {
                // 啟動地圖元件用的Intent物件
                val intentMap = Intent(this, MapsActivity::class.java)
                // 啟動地圖元件
                startActivityForResult(intentMap, ItemAction.LOCATION.ordinal)
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

    // 改為可以使用類別名稱呼叫這個函式
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

    // 拍攝照片
    private fun takePicture() {
        // 取得照片檔案物件
        val file = getFileName("P", ".jpg")
        val uri : Uri

        // 如果是LOLLIPOP_MR1或更新的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            // 使用FileProvider建立Uri物件
            uri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file)
        }
        else {
            uri = Uri.fromFile(file)
        }

        // 啟動相機元件用的Intent物件
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // 設定檔案名稱
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        // 啟動相機元件
        startActivityForResult(intentCamera, ItemAction.CAMERA.ordinal)
    }

    // 取得照片檔案名稱物件
    private fun getFileName(prefix: String, extension: String): File {
        // 如果記事資料已經有照片檔案名稱
        if (!item.fileName.isNullOrEmpty()) {
            pictureFileName = item.fileName
        }
        // 產生檔案名稱
        else {
            pictureFileName = getUniqueFileName()
        }

        // 儲存照片的目錄
        val photoPath = File(Environment.getExternalStorageDirectory(), "photo")

        if (!photoPath.exists()) {
            // 建立儲存照片的目錄
            photoPath.mkdir()
        }

        // 傳回照片檔案物件
        return File(photoPath, "$prefix$pictureFileName$extension")
    }

    // 讀取與處理寫入外部儲存設備授權請求
    private fun requestStoragePermission() {
        // 如果裝置版本是6.0（包含）以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 取得授權狀態，參數是請求授權的名稱
            val hasPermission = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            // 如果未授權
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                // 請求授權
                //     第一個參數是請求授權的名稱
                //     第二個參數是請求代碼
                requestPermissions(
                        arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION)
                return
            }
        }

        // 如果裝置版本是6.0以下，
        // 或是裝置版本是6.0（包含）以上，使用者已經授權，
        // 拍攝照片
        takePicture()
    }

    override fun onRequestPermissionsResult(requestCode : Int,
                                            permissions : Array<String>,
                                            grantResults : IntArray) {
        // 如果是寫入外部儲存設備授權請求
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            // 如果在授權請求選擇「允許」
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 拍攝照片
                takePicture()
            }
            // 如果在授權請求選擇「拒絕」
            else {
                Toast.makeText(this, R.string.write_external_storage_denied,
                        Toast.LENGTH_SHORT).show()
            }
        }
        // 如果是使用錄音設備授權請求
        else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            // 如果在授權請求選擇「允許」
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 錄音或播放
                processRecord();
            }
            // 如果在授權請求選擇「拒絕」
            else {
                // 顯示沒有授權的訊息
                Toast.makeText(this, R.string.record_audio_denied,
                        Toast.LENGTH_SHORT).show();
            }
        }

        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // 錄音與播放
    fun processRecord() {
        // 錄音檔案名稱
        val recordFile = getRecFileName("R", ".mp3")

        // 如果已經有錄音檔，詢問播放或重新錄製
        if (recordFile.exists()) {
            // 詢問播放還是重新錄製的對話框
            val d = AlertDialog.Builder(this)

            d.setTitle(R.string.title_record)
                    .setCancelable(false)
            d.setPositiveButton(R.string.record_play,
                    DialogInterface.OnClickListener { dialog, which ->
                        // 啟動播放元件
                        val playIntent : Intent  = Intent(
                                this, PlayActivity::class.java)
                        playIntent.putExtra("fileName",
                                recordFile.getAbsolutePath());
                        startActivity(playIntent);
                    })

            d.setNeutralButton(R.string.record_new,
                    DialogInterface.OnClickListener { dialog, which ->
                        // 重新錄音
                        val recordIntent = Intent(this@ItemActivity, RecordActivity::class.java)
                        recordIntent.putExtra("fileName", recordFile.absolutePath)
                        startActivityForResult(recordIntent, ItemAction.RECORD.ordinal)
                    })

            d.setNegativeButton(android.R.string.cancel, null)

            // 顯示對話框
            d.show()
        }
        // 如果沒有錄音檔，啟動錄音元件
        else {
            // 錄音
            val recordIntent = Intent(this, RecordActivity::class.java)
            recordIntent.putExtra("fileName", recordFile.absolutePath)
            startActivityForResult(recordIntent, ItemAction.RECORD.ordinal)
        }
    }

    private fun getRecFileName(prefix: String, extension: String): File {
        // 如果記事資料已經有錄音檔案名稱
        if (!item.recFileName.isNullOrBlank()) {
            recFileName = item.recFileName
        } else {
            // 產生檔案名稱
            recFileName = getUniqueFileName()
        }

        // 儲存錄音的目錄
        val recordPath = File(Environment.getExternalStorageDirectory(), "record")

        if (!recordPath.exists()) {
            // 建立儲存錄音的目錄
            recordPath.mkdir()
        }

        // 傳回錄音檔案物件
        return File(recordPath, "$prefix$recFileName$extension")
    }

    // 讀取與處理錄音設備授權請求
    private fun requestRecordPermission() {
        // 如果裝置版本是6.0（包含）以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 取得授權狀態，參數是請求授權的名稱
            val hasPermission = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.RECORD_AUDIO)

            // 如果未授權
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                // 請求授權
                //     第一個參數是請求授權的名稱
                //     第二個參數是請求代碼
                requestPermissions(
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        REQUEST_RECORD_AUDIO_PERMISSION)
                return
            }
        }

        // 如果裝置版本是6.0以下，
        // 或是裝置版本是6.0（包含）以上，使用者已經授權，
        // 錄音或播放
        processRecord()
    }

}
