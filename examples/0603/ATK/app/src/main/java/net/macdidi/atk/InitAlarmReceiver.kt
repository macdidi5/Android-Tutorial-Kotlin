package net.macdidi.atk

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.*

class InitAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 建立資料庫物件
        val itemDAO = ItemDAO(context.applicationContext)
        // 讀取資料庫所有記事資料
        val items = itemDAO.all
        // 讀取目前時間
        val current = Calendar.getInstance().timeInMillis

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (item in items) {
            // 如果有設定提醒而且提醒還沒有過期
            if (item.alarmDatetime != 0L && item.alarmDatetime > current) {
                // 設定提醒
                val alarmIntent = Intent(context, AlarmReceiver::class.java)
                // 移除原來的記事標題資料
                //alarmIntent.putExtra("title", item.title)

                // 加入記事編號資料
                intent.putExtra("id", item.id)

                val pi = PendingIntent.getBroadcast(
                        context, item.id.toInt(),
                        alarmIntent, PendingIntent.FLAG_ONE_SHOT)
                am.set(AlarmManager.RTC_WAKEUP, item.alarmDatetime, pi)
            }
        }
    }

}
