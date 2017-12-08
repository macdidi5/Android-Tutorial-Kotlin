package net.macdidi.atk

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View

// 一般畫面元件
//   參數為畫面元件編號
//   回傳畫面元件物件
fun <T : View> Activity.bind(@IdRes res : Int) : Lazy<T> {
    return lazy { findViewById<T>(res) }
}

// 自定畫面元件
//   參數為畫面元件編號
//   回傳畫面元件物件
fun <T : View> View.bind(@IdRes res : Int) : Lazy<T> {
    return lazy { findViewById<T>(res) }
}