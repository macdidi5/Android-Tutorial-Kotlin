package net.macdidi.atk

import android.app.Activity
import android.content.Intent
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

    // 換掉原來的字串陣列
    private val data = ArrayList<String>()

    private val adapter : ArrayAdapter<String>
        by lazy {ArrayAdapter(this, android.R.layout.simple_list_item_1, data)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        processControllers()

        // 加入範例資料
        data.add("關於Android Tutorial的事情");
        data.add("一隻非常可愛的小狗狗!");
        data.add("一首非常好聽的音樂！");

        item_list.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int, data: Intent) {
        // 如果被啟動的Activity元件傳回確定的結果
        if (resultCode == Activity.RESULT_OK) {
            val titleText = data.getStringExtra("titleText")

            // 如果是新增記事
            if (requestCode == 0) {
                // 加入標題項目
                this.data.add(titleText)
                // 通知資料已經改變，ListView元件才會重新顯示
                adapter.notifyDataSetChanged()
            }
            // 如果是修改記事
            else if (requestCode == 1) {
                // 讀取記事編號
                val position = data.getIntExtra("position", -1)

                if (position != -1) {
                    // 設定標題項目
                    this.data[position] = titleText
                    // 通知資料已經改變，ListView元件才會重新顯示
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun processControllers() {
        val itemListener = AdapterView.OnItemClickListener {
            // position: 使用者選擇的項目編號，第一個是0
            _, _, position, _ ->
            // 使用Action名稱建立啟動另一個Activity元件需要的Intent物件
            val intent = Intent("net.macdidi.atk.EDIT_ITEM")

            // 設定記事編號與標題
            intent.putExtra("position", position)
            intent.putExtra("titleText", data[position])

            // 呼叫「startActivityForResult」，第二個參數「1」表示執行修改
            startActivityForResult(intent, 1)
        }

        item_list.onItemClickListener = itemListener

        val itemLongListener = AdapterView.OnItemLongClickListener {
            // position: 使用者選擇的項目編號，第一個是0
            _, _, position, _ ->
            Toast.makeText(this@MainActivity,
                    "Long: ${data[position]}", Toast.LENGTH_LONG).show()
            false
        }

        item_list.onItemLongClickListener = itemLongListener

        val listener = View.OnLongClickListener {
            val dialog = AlertDialog.Builder(this@MainActivity)
            dialog.setTitle(R.string.app_name)
                    .setMessage(R.string.about)
                    .show()
            false
        }

        show_app_name.setOnLongClickListener(listener)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun clickMenuItem(item: MenuItem) {
        // 判斷該執行什麼工作，目前還沒有加入需要執行的工作
        when (item.itemId) {
            R.id.search_item -> {
            }
            // 使用者選擇新增選單項目
            R.id.add_item -> {
                // 使用Action名稱建立啟動另一個Activity元件需要的Intent物件
                val intent = Intent("net.macdidi.atk.ADD_ITEM")
                // 呼叫「startActivityForResult」，，第二個參數「0」表示執行新增
                startActivityForResult(intent, 0)
            }
            R.id.revert_item -> {
            }
            R.id.delete_item -> {
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

}
