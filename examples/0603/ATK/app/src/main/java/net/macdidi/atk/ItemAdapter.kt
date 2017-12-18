package net.macdidi.atk

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class ItemAdapter(context: Context,
                  private val resource: Int,
                  private val items: MutableList<Item>)
        : ArrayAdapter<Item>(context, resource, items) {

    override fun getView(position: Int,
                         convertView: View?,
                         parent: ViewGroup): View {
        val itemView: LinearLayout
        // 讀取目前位置的記事物件
        val item = getItem(position)

        if (convertView == null) {
            // 建立項目畫面元件
            itemView = LinearLayout(context)
            val inflater = Context.LAYOUT_INFLATER_SERVICE
            val li = context.getSystemService(inflater) as LayoutInflater
            li.inflate(resource, itemView, true)
        } else {
            itemView = convertView as LinearLayout
        }

        // 讀取記事顏色、已選擇、標題與日期時間元件
        val typeColor : RelativeLayout = itemView.findViewById(R.id.type_color)
        val selectedItem : ImageView = itemView.findViewById(R.id.selected_item)
        val titleView : TextView = itemView.findViewById(R.id.title_text)
        val dateView : TextView = itemView.findViewById(R.id.date_text)

        // 設定記事顏色
        val background = typeColor.background as GradientDrawable
        background.setColor(item.color.parseColor())

        // 設定標題與日期時間
        titleView.text = item.title
        dateView.text = item.localeDatetime

        // 設定是否已選擇
        selectedItem.visibility = if (item.isSelected) View.VISIBLE else View.INVISIBLE

        return itemView
    }

    // 設定指定編號的記事資料
    operator fun set(index: Int, item: Item) {
        if (index >= 0 && index < items.size) {
            items[index] = item
            notifyDataSetChanged()
        }
    }

    // 讀取指定編號的記事資料
    operator fun get(index: Int): Item {
        return items[index]
    }

}