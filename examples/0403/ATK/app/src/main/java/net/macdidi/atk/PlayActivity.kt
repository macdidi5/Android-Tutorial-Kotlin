package net.macdidi.atk

import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View

// 從AppCompatActivity改為Activity
class PlayActivity : Activity() {

    // 播放元件
    private lateinit var mediaPlayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        // 讀取與設定錄音檔案名稱
        val fileName : String = intent.getStringExtra("fileName")
        val uri : Uri = Uri.parse(fileName)
        mediaPlayer = MediaPlayer.create(this, uri)
    }

    override fun onStop() {
        if (mediaPlayer.isPlaying) {
            // 停止播放
            mediaPlayer.stop()
        }

        // 清除MediaPlayer物件
        mediaPlayer.release()
        super.onStop()
    }

    fun onSubmit(view: View) {
        // 結束Activity元件
        finish()
    }

    fun clickPlay(view: View) {
        // 開始播放
        mediaPlayer.start()
    }

    fun clickPause(view: View) {
        // 暫停播放
        mediaPlayer.pause()
    }

    fun clickStop(view: View) {
        // 停止播放
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        // 回到開始的位置
        mediaPlayer.seekTo(0)
    }

}
