package net.macdidi.atk

import android.app.Activity
import android.app.ActivityOptions
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import java.util.*

// 從Activity改為AppCompatActivity
class MainActivity : AppCompatActivity() {

    // 移除原來的ListView元件
    //private val item_list : ListView by bind(R.id.item_list)

    // 加入下列需要的元件
    private val item_list: RecyclerView by bind(R.id.item_list)
    private lateinit var itemAdapter: RecyclerView.Adapter<ItemAdapterRV.ViewHolder>
    private val rvLayoutManager: RecyclerView.LayoutManager
            by lazy { LinearLayoutManager(this) }

    private val show_app_name: TextView by bind(R.id.show_app_name)

    // 移除原來的ItemAdapter
    //private val itemAdapter: ItemAdapter
    //        by lazy { ItemAdapter(this, R.layout.single_item, items) }

    // 儲存所有記事本的List物件
    private val items: ArrayList<Item> = ArrayList()

    // 選單項目物件
    private lateinit var add_item: MenuItem
    private lateinit var search_item: MenuItem
    private lateinit var revert_item: MenuItem
    private lateinit var delete_item: MenuItem

    // 已選擇項目數量
    private var selectedCount = 0

    // 宣告資料庫功能類別欄位變數
    private val itemDAO : ItemDAO by lazy { ItemDAO(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 移除註冊監聽事件的工作，要移到下面執行
        //processControllers()

        // 如果資料庫是空的，就建立一些範例資料
        // 這是為了方便測試用的，完成應用程式以後可以拿掉
        if (itemDAO.count == 0) {
            itemDAO.createSampleData()
        }

        // 取得所有記事資料
        items.addAll(itemDAO.all)

        // 移除原來ListView元件執行的工作
        //item_list.adapter = itemAdapter

        // 執行RecyclerView元件的設定
        item_list.setHasFixedSize(true)
        item_list.layoutManager = rvLayoutManager

        // 在這裡執行註冊監聽事件的工作
        processControllers()
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent) {
        // 如果被啟動的Activity元件傳回確定的結果
        if (resultCode == Activity.RESULT_OK) {
            // 讀取記事物件
            val item = data.extras.getSerializable(
                    "net.macdidi.atk.Item") as Item

            // 是否修改提醒設定
            var updateAlarm = false

            // 如果是新增記事
            if (requestCode == 0) {
                // 新增記事資料到資料庫
                val itemNew : Item = itemDAO.insert(item)
                // 設定記事物件的編號
                item.id = itemNew.id

                // 加入新增的記事物件
                items.add(item)

                // 通知資料改變
                itemAdapter.notifyDataSetChanged()

                // 設定為已修改提醒
                updateAlarm = true
            }
            // 如果是修改記事
            else if (requestCode == 1) {
                // 讀取記事編號
                val position = data.getIntExtra("position", -1)

                if (position != -1) {
                    // 讀取原來的提醒設定
                    val ori = itemDAO[item.id]
                    // 判斷是否需要設定提醒
                    updateAlarm = item.alarmDatetime != ori?.alarmDatetime


                    // 修改資料庫中的記事資料
                    itemDAO.update(item)

                    // 設定修改的記事物件
                    items.set(position, item)
                    itemAdapter.notifyDataSetChanged()
                }
            }

            // 設定提醒
            if (item.alarmDatetime != 0L && updateAlarm) {
                val intent = Intent(this, AlarmReceiver::class.java)
                // 移除原來的記事標題與訊息框
                //intent.putExtra("title", item.title)

                // 加入記事編號資料
                intent.putExtra("id", item.id)

                val pi = PendingIntent.getBroadcast(
                        this, item.id.toInt(),
                        intent, PendingIntent.FLAG_ONE_SHOT)

                val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                am.set(AlarmManager.RTC_WAKEUP, item.alarmDatetime, pi)
            }

        }
    }

    private fun processControllers() {

        // 實作ItemAdapterRV類別，加入註冊監聽事件的工作
        itemAdapter = object : ItemAdapterRV(items) {
            override fun onBindViewHolder(holder: ItemAdapterRV.ViewHolder,
                                          position: Int) {
                super.onBindViewHolder(holder, position)

                // 建立與註冊項目點擊監聽物件
                holder.rootView.setOnClickListener {
                    // 讀取選擇的記事物件
                    val item = items[position]

                    // 如果已經有勾選的項目
                    if (selectedCount > 0) {
                        // 處理是否顯示已選擇項目
                        processMenu(item)
                        // 重新設定記事項目
                        items[position] = item
                    } else {
                        val intent = Intent(
                                "net.macdidi.atk.EDIT_ITEM")

                        // 設定記事編號與記事物件
                        intent.putExtra("position", position)
                        intent.putExtra("net.macdidi.atk.Item", item)

                        // 依照版本啟動Activity元件
                        startActivityForVersion(intent, 1)
                    }
                }

                // 建立與註冊項目長按監聽物件
                holder.rootView.setOnLongClickListener {
                    // 讀取選擇的記事物件
                    val item = items[position]
                    // 處理是否顯示已選擇項目
                    processMenu(item)
                    // 重新設定記事項目
                    items[position] = item
                    true
                }
            }
        }

        // 設定RecyclerView使用的資料來源
        item_list.adapter = itemAdapter
    }

    // 處理是否顯示已選擇項目
    private fun processMenu(item: Item?) {
        // 如果需要設定記事項目
        if (item != null) {
            // 設定已勾選的狀態
            item.isSelected = !item.isSelected

            // 計算已勾選數量
            if (item.isSelected) {
                selectedCount++
            } else {
                selectedCount--
            }
        }

        // 根據選擇的狀況，設定是否顯示選單項目
        add_item.setVisible(selectedCount == 0)
        search_item.setVisible(selectedCount == 0)
        revert_item.setVisible(selectedCount > 0)
        delete_item.setVisible(selectedCount > 0)

        // 通知項目勾選狀態改變
        itemAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        // 取得選單項目物件
        add_item = menu.findItem(R.id.add_item)
        search_item = menu.findItem(R.id.search_item)
        revert_item = menu.findItem(R.id.revert_item)
        delete_item = menu.findItem(R.id.delete_item)

        // 設定選單項目
        processMenu(null)

        return true
    }

    fun clickMenuItem(item: MenuItem) {
        // 判斷該執行什麼工作
        when (item.itemId) {
            R.id.search_item -> {
            }
            // 使用者選擇新增選單項目
            R.id.add_item -> {
                // 使用Action名稱建立啟動另一個Activity元件需要的Intent物件
                val intent = Intent("net.macdidi.atk.ADD_ITEM")
                // 改為呼叫這個函式，依照版本啟動Activity元件
                startActivityForVersion(intent, 0)
            }
            // 取消所有已勾選的項目
            R.id.revert_item -> {
                // 改為使用items物件
                for (i in 0 until items.size) {
                    val ri = items[i]

                    if (ri.isSelected) {
                        ri.isSelected = false
                        // 移除
                        //items[i] = ri
                    }
                }

                selectedCount = 0
                processMenu(null)
            }
            // 刪除
            R.id.delete_item -> {
                // 沒有選擇
                if (selectedCount == 0) {
                    return
                }

                // 建立與顯示詢問是否刪除的對話框
                val d = AlertDialog.Builder(this)
                val message = getString(R.string.delete_item)
                d.setTitle(R.string.delete)
                        .setMessage(String.format(message, selectedCount))
                d.setPositiveButton(android.R.string.yes) { _, _ ->
                    // 改為使用items物件
                    var index = items.size - 1

                    while (index > -1) {
                        val item = items[index]

                        if (item.isSelected) {
                            items.remove(item)
                            // 刪除資料庫中的記事資料
                            itemDAO.delete(item.id)
                        }

                        index--
                    }

                    // 移除
                    //itemAdapter.notifyDataSetChanged()
                    selectedCount = 0
                    processMenu(null)
                }
                d.setNegativeButton(android.R.string.no, null)
                d.show()
            }
        }
    }

    fun aboutApp(view: View) {
        // 建立啟動另一個Activity元件需要的Intent物件
        // 建構式的第一個參數：「this」
        // 建構式的第二個參數：「Activity元件類別名稱::class.java」
        val intent = Intent(this, AboutActivity::class.java)
        // 呼叫「startActivity」，參數為一個建立好的Intent物件
        // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
        startActivity(intent)
    }

    // 設定
    fun clickPreferences(item: MenuItem) {
        // 改為呼叫這個函式，依照版本啟動Activity元件
        startActivityForVersion(Intent(this, PrefActivity::class.java))
    }

    private fun startActivityForVersion(intent: Intent, requestCode: Int) {
        // 如果裝置的版本是LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 加入畫面轉換設定
            startActivityForResult(intent, requestCode,
                    ActivityOptions.makeSceneTransitionAnimation(
                            this@MainActivity).toBundle())
        } else {
            startActivityForResult(intent, requestCode)
        }
    }

    private fun startActivityForVersion(intent: Intent) {
        // 如果裝置的版本是LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 加入畫面轉換設定
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(
                            this@MainActivity).toBundle())
        } else {
            startActivity(intent)
        }
    }

    // 點擊新增按鈕
    fun clickAdd(view: View) {
        val intent = Intent("net.macdidi.atk.ADD_ITEM")
        startActivityForVersion(intent, 0)
    }

}
