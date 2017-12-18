package net.macdidi.atk

import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

open class ItemAdapterRV(private val items: MutableList<Item>)
        : RecyclerView.Adapter<ItemAdapterRV.ViewHolder>() {

    override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): ItemAdapterRV.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
                R.layout.single_item, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ItemAdapterRV.ViewHolder, position: Int) {
        val item = items[position]

        // 設定記事顏色
        val background = holder.typeColor.background as GradientDrawable
        background.setColor(item.color.parseColor())

        // 設定標題與日期時間
        holder.titleView.text = item.title
        holder.dateView.text = item.localeDatetime

        // 設定是否已選擇
        holder.selectedItem.visibility =
                if (item.isSelected) View.VISIBLE else View.INVISIBLE
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun add(item: Item) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    // 一定要使用ViewHolder包裝畫面元件
    inner class ViewHolder(var rootView: View)
            : RecyclerView.ViewHolder(rootView) {

        var typeColor: RelativeLayout = itemView.findViewById(R.id.type_color)
        var selectedItem: ImageView = itemView.findViewById(R.id.selected_item)
        var titleView: TextView = itemView.findViewById(R.id.title_text)
        var dateView: TextView = itemView.findViewById(R.id.date_text)

    }

}
