package net.macdidi.atk

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout


class ColorActivity : Activity() {

    private val color_gallery: LinearLayout by bind(R.id.color_gallery)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)

        val listener = ColorListener()

        for (c in Colors.values()) {
            val button = Button(this)
            button.setId(c.parseColor())
            val layout = LinearLayout.LayoutParams(128, 128)
            layout.setMargins(6, 6, 6, 6)
            button.setLayoutParams(layout)
            button.setBackgroundColor(c.parseColor())

            button.setOnClickListener(listener)

            color_gallery.addView(button)
        }
    }

    private inner class ColorListener : View.OnClickListener {

        override fun onClick(view: View) {
            intent.putExtra("colorId", view.getId())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }
}
