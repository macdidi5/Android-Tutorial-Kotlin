package net.macdidi.atk

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// 讀取指定的照片檔案名稱設定給ImageView元件
fun fileToImageView(fileName: String, imageView: ImageView) {
    if (File(fileName).exists()) {
        val bitmap = BitmapFactory.decodeFile(fileName)
        imageView.setImageBitmap(bitmap)
    } else {
        Log.e("fileToImageView", fileName + " not found.")
    }
}

// 產生唯一的檔案名稱
fun getUniqueFileName(): String {
    // 使用年月日_時分秒格式為檔案名稱
    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
    return sdf.format(Date())
}