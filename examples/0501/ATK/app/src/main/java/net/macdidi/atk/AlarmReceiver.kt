package net.macdidi.atk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 讀取記事標題
        val title = intent.getStringExtra("title")
        // 顯示訊息框
        Toast.makeText(context, title, Toast.LENGTH_LONG).show()
    }

}
