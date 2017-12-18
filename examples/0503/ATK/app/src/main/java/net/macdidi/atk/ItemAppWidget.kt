package net.macdidi.atk

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class ItemAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context,
                          appWidgetManager: AppWidgetManager,
                          appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val N = appWidgetIds.size

        for (i in 0 until N) {
            // 刪除小工具已經儲存的記事編號
            ItemAppWidgetConfigureActivity.deleteItemPref(
                    context, appWidgetIds[i])
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {

    }

    companion object {

        internal fun updateAppWidget(context: Context,
                                     appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            // 讀取小工具儲存的記事編號
            val id = ItemAppWidgetConfigureActivity.loadItemPref(
                    context, appWidgetId)
            // 建立小工具畫面元件
            val views = RemoteViews(
                    context.packageName, R.layout.item_app_widget)
            // 讀取指定編號的記事物件
            val itemDAO = ItemDAO(context.applicationContext)
            val item = itemDAO[id]

            // 設定小工具畫面顯示記事標題
            views.setTextViewText(R.id.appwidget_text,
                    item?.title ?: "NA")

            // 點選小工具畫面的記事標題後，啟動記事應用程式
            val intent = Intent(context, MainActivity::class.java)
            val pending = PendingIntent.getActivity(
                    context, 0, intent, 0)
            views.setOnClickPendingIntent(R.id.appwidget_text, pending)

            // 更新小工具
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

}

