package net.macdidi.atk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 為ListView元件設定三筆資料
        val data = arrayOf("關於Android Tutorial的事情",
                "一隻非常可愛的小狗狗!", "一首非常好聽的音樂！")
        val layoutId = android.R.layout.simple_list_item_1
        val adapter = ArrayAdapter(this, layoutId, data)
        val item_list: ListView = findViewById(R.id.item_list)
        item_list.setAdapter(adapter)
    }

    // 加入載入選單資源的方法
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

}
