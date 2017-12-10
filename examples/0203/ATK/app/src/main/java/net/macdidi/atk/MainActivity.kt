package net.macdidi.atk

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*

class MainActivity : AppCompatActivity() {

    private val item_list : ListView by bind(R.id.item_list)
    private val show_app_name: TextView by bind(R.id.show_app_name)

    private val data = arrayOf("關於Android Tutorial的事情",
            "一隻非常可愛的小狗狗!", "一首非常好聽的音樂！")
    private val adapter : ArrayAdapter<String>
        by lazy { ArrayAdapter(this, android.R.layout.simple_list_item_1, data) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        processControllers()

        item_list.adapter = adapter
    }

    private fun processControllers() {
        // 建立選單項目點擊監聽物件
        val itemListener = AdapterView.OnItemClickListener {
            // parent: 使用者操作的ListView物件
            // view: 使用者選擇的項目
            // position: 使用者選擇的項目編號，第一個是0
            // id: 在這裡沒有用途            
            parent, view, position, id ->
            Toast.makeText(this@MainActivity,
                    data[position], Toast.LENGTH_LONG).show()
        }

        // 註冊選單項目點擊監聽物件
        item_list.onItemClickListener = itemListener

        // 建立選單項目長按監聽物件
        val itemLongListener = AdapterView.OnItemLongClickListener {
            // parent: 使用者操作的ListView物件
            // view: 使用者選擇的項目
            // position: 使用者選擇的項目編號，第一個是0
            // id: 在這裡沒有用途
            parent, view, position, id ->
            Toast.makeText(this@MainActivity,
                    "Long: ${data[position]}", Toast.LENGTH_LONG).show()
            false
        }

        // 註冊選單項目長按監聽物件
        item_list.onItemLongClickListener = itemLongListener

        // 建立長按監聽物件
        val listener = View.OnLongClickListener {
            val dialog = AlertDialog.Builder(this@MainActivity)
            dialog.setTitle(R.string.app_name)
                    .setMessage(R.string.about)
                    .show()
            false
        }

        // 註冊長按監聽物件
        show_app_name.setOnLongClickListener(listener)
    }

    // 載入選單資源
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 使用者選擇所有的選單項目都會呼叫這個函式
    fun clickMenuItem(item: MenuItem) {
        // 使用參數取得使用者選擇的選單項目元件編號
        val itemId = item.itemId

        // 判斷該執行什麼工作，目前還沒有加入需要執行的工作
        when (itemId) {
            R.id.search_item -> {
            }
            R.id.add_item -> {
            }
            R.id.revert_item -> {
            }
            R.id.delete_item -> {
            }
        }

        // 測試用的程式碼，完成測試後記得移除
        val dialog = AlertDialog.Builder(this@MainActivity)
        dialog.setTitle("MenuItem Test")
                .setMessage(item.title)
                .setIcon(item.icon)
                .show()
    }

    // 函式名稱與onClick的設定一樣，參數的型態是android.view.View
    fun aboutApp(view: View) {
        // 顯示訊息框
        // Context：通常指定為「this」；如果在巢狀類別中使用，要加上這個Activity元件類別的名稱，例如「元件類別名稱.this」
        // String或int：設定顯示在訊息框裡面的訊息或文字資源
        // int：設定訊息框停留在畫面的時間，使用宣告在Toast類別中的變數，可以設定為「LENGTH_LONG」和「LENGTH_SHORT」
        Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show()
    }

}
