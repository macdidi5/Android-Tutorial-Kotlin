package net.macdidi.atk

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView

class ItemAppWidgetConfigureActivity : Activity() {

    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    // 選擇小工具使用的記事項目
    private val item_list: ListView by bind(R.id.item_list)
    private val itemDAO: ItemDAO by lazy { ItemDAO(applicationContext) }
    private val items: ArrayList<Item> by lazy { itemDAO.all }
    private val itemAdapter: ItemAdapter
            by lazy { ItemAdapter(this, R.layout.single_item, items) }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        setResult(Activity.RESULT_CANCELED)

        // 改為使用應用程式主畫面
        setContentView(R.layout.activity_main)

        // 建立與設定選擇小工具使用的記事項目需要的物件
        item_list.adapter = itemAdapter
        item_list.onItemClickListener = itemListener

        val extras = intent.extras

        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }

    companion object {

        private val PREFS_NAME = "net.macdidi.atk.ItemAppWidget"
        private val PREF_PREFIX_KEY = "appwidget_"

        // 儲存選擇的記事編號
        fun saveItemPref(context: Context, appWidgetId: Int, id: Long) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putLong(PREF_PREFIX_KEY + appWidgetId, id)
            prefs.commit()
        }

        // 讀取記事編號
        fun loadItemPref(context: Context, appWidgetId: Int): Long {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)

            return prefs.getLong(PREF_PREFIX_KEY + appWidgetId, 0)
        }

        // 刪除記事編號
        fun deleteItemPref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId)
            prefs.commit()
        }

    }

    // 選擇記事項目
    internal var itemListener: AdapterView.OnItemClickListener =
            AdapterView.OnItemClickListener {
                _, _, position, _ ->
        val context = this@ItemAppWidgetConfigureActivity

        // 讀取與儲存選擇的記事物件
        val item = itemAdapter.getItem(position)
        saveItemPref(context, mAppWidgetId, item.id)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        ItemAppWidget.updateAppWidget(
                context, appWidgetManager, mAppWidgetId)
        val resultValue = Intent()
        resultValue.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)

        finish()
    }

}

