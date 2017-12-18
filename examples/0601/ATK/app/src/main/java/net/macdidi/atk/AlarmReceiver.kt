package net.macdidi.atk

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.support.v4.app.NotificationCompat
import java.io.File

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 移除原來的記事標題與訊息框
        // 讀取記事標題
        //val title = intent.getStringExtra("title")
        // 顯示訊息框
        //Toast.makeText(context, title, Toast.LENGTH_LONG).show()

        // 讀取記事編號
        val id = intent.getLongExtra("id", 0)

        if (id != 0L) {
            sendNotify(context, id)
        }
    }

    private fun sendNotify(context: Context, id: Long) {
        // 建立資料庫物件
        val itemDAO = ItemDAO(context.applicationContext)
        // 讀取指定編號的記事物件
        val item = itemDAO[id]

        // 建立照片檔案物件
        // 儲存照片的目錄
        val photoPath = File(Environment.getExternalStorageDirectory(), "photo")

        val file = File(photoPath, "P${item!!.fileName}.jpg")

        // 是否儲存照片檔案
        val pictureExist = item!!.fileName != null &&
                item.fileName!!.length > 0 &&
                file.exists()

        // 取得NotificationManager物件
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = "net.macdidi.atk.alarmnotify"
        createChannel(nm, channel, "AlarmNotify", "ATK alarm notify notify channel");

        // 建立NotificationCompat.Builder物件
        val builder = NotificationCompat.Builder(context, channel)

        // 如果有儲存照片檔案
        if (pictureExist) {
            builder.setSmallIcon(android.R.drawable.star_on)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(context.getString(R.string.app_name))

            // 建立大型圖片樣式物件
            val bigPictureStyle = NotificationCompat.BigPictureStyle()
            // 設定圖片與簡介
            val bitmap = BitmapFactory.decodeFile(file.getAbsolutePath())
            bigPictureStyle.bigPicture(bitmap)
                    .setSummaryText(item.title)
            // 設定樣式為大型圖片
            builder.setStyle(bigPictureStyle)
        }
        // 如果沒有儲存照片檔案
        else {
            // 設定圖示、時間、內容標題和內容訊息
            builder.setSmallIcon(android.R.drawable.star_big_on)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(item.title)
        }

        // 發出通知
        nm.notify(item.id.toInt(), builder.build())
    }

    // 建立與設定Notify channel
    // 加入裝置版本的判斷，應用程式就不用把最低版本設定為API level 26
    private fun createChannel(nm : NotificationManager,
                              id: String, name:
                              String, desc: String) {
        // 如果系統版本低於 Android 8.0 （API level 26）就不執行設定
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        // 建立channel物件，參數依序為channel代碼、名稱與等級
        val nChannel = NotificationChannel(
                id, name, NotificationManager.IMPORTANCE_DEFAULT)
        // 設定channel的說明
        nChannel.description = desc
        // 設定channel物件
        nm.createNotificationChannel(nChannel)
    }

}
