package net.macdidi.atk

import android.app.Activity
import android.media.MediaRecorder
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import java.io.IOException

// 從AppCompatActivity改為Activity
class RecordActivity : Activity() {

    private val record_button : ImageButton by bind(R.id.record_button)
    private var isRecording : Boolean = false
    private val record_volumn : ProgressBar by bind(R.id.record_volumn)

    private lateinit var fileName : String
    private var myRecoder: MyRecoder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        // 隱藏狀態列ProgressBar
        setProgressBarIndeterminateVisibility(false);

        // 讀取檔案名稱
        fileName = intent.getStringExtra("fileName")
    }

    fun onSubmit(view: View) {
        if (isRecording) {
            // 停止錄音
            myRecoder?.stop()
        }

        // 確定
        if (view.getId() === R.id.record_ok) {
            setResult(Activity.RESULT_OK, intent)
        }

        finish()
    }

    fun clickRecord(view: View) {
        // 切換
        isRecording = !isRecording

        // 開始錄音
        if (isRecording) {
            // 設定按鈕圖示為錄音中
            record_button.setImageResource(R.drawable.record_red_icon)
            // 建立錄音物件
            myRecoder = MyRecoder(fileName)
            // 開始錄音
            myRecoder?.start()
            // 建立並執行顯示麥克風音量的AsyncTask物件
            MicLevelTask().execute()
        }
        // 停止錄音
        else {
            // 設定按鈕圖示為停止錄音
            record_button.setImageResource(R.drawable.record_dark_icon)
            // 麥克風音量歸零
            record_volumn.progress = 0
            // 停止錄音
            myRecoder?.stop()
        }
    }

    // 在錄音過程中顯示麥克風音量
    private inner class MicLevelTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg args: Void): Void? {
            while (isRecording) {
                publishProgress()

                try {
                    Thread.sleep(200)
                } catch (e: InterruptedException) {
                    Log.d("RecordActivity", e.toString())
                }

            }

            return null
        }

        override fun onProgressUpdate(vararg values: Void) {
            record_volumn.progress = myRecoder?.amplitudeEMA?.toInt() ?: 0
        }

    }

    // 執行錄音並且可以取得麥克風音量的錄音物件
    private inner class MyRecoder internal constructor(private val output: String) {

        private var recorder: MediaRecorder? = null
        private var mEMA = 0.0
        private val EMA_FILTER = 0.6

        val amplitude: Double
            get() = if (recorder != null)
                recorder!!.maxAmplitude / 2700.0
            else
                0.0

        // 取得麥克風音量
        val amplitudeEMA: Double
            get() {
                val amp = amplitude
                mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA
                return mEMA
            }

        // 開始錄音
        fun start() {
            if (recorder == null) {
                // 建立錄音用的MediaRecorder物件
                recorder = MediaRecorder()
                // 設定錄音來源為麥克風，必須在setOutputFormat函式之前呼叫
                recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
                // 設定輸出格式為3GP壓縮格式，必須在setAudioSource函式之後，
                // 在prepare函式之前呼叫
                recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                // 設定錄音的編碼方式，必須在setOutputFormat函式之後，
                // 在prepare函式之前呼叫
                recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                // 設定輸出的檔案名稱，必須在setOutputFormat函式之後，
                // 在prepare函式之前呼叫
                recorder!!.setOutputFile(output)

                try {
                    // 準備執行錄音工作，必須在所有設定之後呼叫
                    recorder!!.prepare()
                } catch (e: IOException) {
                    Log.d("RecordActivity", e.toString())
                }

                // 開始錄音
                recorder!!.start()
                mEMA = 0.0
            }
        }

        // 停止錄音
        fun stop() {
            if (recorder != null) {
                // 停止錄音
                recorder!!.stop()
                // 清除錄音資源
                recorder!!.release()
                recorder = null
            }
        }
    }

}
