package edu.put.inf151860

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FullScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)

        val thumbnail_bmp = intent.getParcelableExtra<android.graphics.Bitmap>("thumbnail_bmp")
        findViewById<android.widget.ImageView>(R.id.imageView).setImageBitmap(thumbnail_bmp)
    }
}