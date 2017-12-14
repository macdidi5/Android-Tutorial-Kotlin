package net.macdidi.atk

import java.util.*

class Item : java.io.Serializable {

    // 編號、日期時間、顏色、標題、內容、照相檔案名稱、錄音檔案名稱、經度、緯度、修改、已選擇
    var id: Long = 0
    var datetime: Long = 0
    var color: Colors
    var title: String
    var content: String
    var fileName: String? = null
    var recFileName: String? = null
    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()
    var lastModify: Long = 0
    var isSelected: Boolean = false

    // 裝置區域的日期時間
    val localeDatetime: String
        get() = String.format(Locale.getDefault(), "%tF  %<tR", Date(datetime))

    // 裝置區域的日期
    val localeDate: String
        get() = String.format(Locale.getDefault(), "%tF", Date(datetime))

    // 裝置區域的時間
    val localeTime: String
        get() = String.format(Locale.getDefault(), "%tR", Date(datetime))

    constructor() {
        title = ""
        content = ""
        color = Colors.LIGHTGREY
    }

    constructor(id: Long, datetime: Long, color: Colors, title: String,
                content: String, fileName: String, recFileName: String,
                latitude: Double, longitude: Double, lastModify: Long) {
        this.id = id
        this.datetime = datetime
        this.color = color
        this.title = title
        this.content = content
        this.fileName = fileName
        this.recFileName = recFileName
        this.latitude = latitude
        this.longitude = longitude
        this.lastModify = lastModify
    }

}