package edu.put.inf151860

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameDetails : AppCompatActivity() {
    var game: Game? = null
    var thumbnail_bmp: android.graphics.Bitmap? = null
    lateinit var dbHandler: MyDBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)

        val game_id = intent.getLongExtra("game_id", -1)
        dbHandler = MyDBHandler(this, null, null, 1)
        game = dbHandler.getGame(game_id)
        thumbnail_bmp = intent.getParcelableExtra("thumbnail_bmp")

        findViewById<ImageView>(R.id.thumbnail).setImageBitmap(thumbnail_bmp)
        findViewById<ImageView>(R.id.thumbnail).setOnClickListener(){
            val intent = Intent(this, FullScreen::class.java)
            intent.putExtra("thumbnail_bmp", thumbnail_bmp)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.title).text = game?.name + " \n\n(" + game?.year + ")"
    }
}