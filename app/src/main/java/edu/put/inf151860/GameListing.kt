package edu.put.inf151860

import android.R.attr.bitmap
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors


class Game(id: Long, title: String?, year: Int?, thumbnailUrl: String?) {
    val ID: Long = id
    val name: String? = title
    val year: Int? = year
    val thumbnailURL: String? = thumbnailUrl
}

class GameRow(context: Context, game: Game, idx: Int, on : Int?) : TableRow(context) {

    init {
        addView(TextView(context).apply {
            if (game.ID == -1L) {
                text = "lp."
            } else {
                text = on.toString()
            }
            layoutParams = LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
            width = 200
        })
        addView(TextView(context).apply {
            if (game.ID == -1L) {
                text = "Tytuł"
            } else {
                text = game.name
            }
            layoutParams = LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
            width = 500
        })
        addView(TextView(context).apply {
            if (game.ID == -1L) {
                text = "Rok"
            } else {
                text = game.year.toString()
            }
            layoutParams = LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f,
            )
            width = 200
        })
        if (game.ID == -1L){
            addView(TextView(context).apply {
                if (game.ID == -1L) {
                    text = "Miniaturka"
                } else {
                    text = game.thumbnailURL
                }
                layoutParams = LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
                width = 500
            })
        } else{
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            executor.execute {
                val url = game.thumbnailURL
                try{
                    val str = java.net.URL(url).openStream()
                    val bmp_ = android.graphics.BitmapFactory.decodeStream(str)
                    val bmp = android.graphics.Bitmap.createScaledBitmap(bmp_, 300, 300, false)
                    handler.post {
                        addView(ImageView(context).apply {
                            layoutParams = LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                            )
                            setImageBitmap(bmp)
                        })
                    }
                } catch (e: Exception){
                    Log.e("GameRow", "Error while loading image: $e")
                }

            }
        }


        apply {
            LayoutParams(
                LayoutParams.WRAP_CONTENT
            )
            if (game.ID == -1L) {
                setBackgroundColor(0xFFAAAAAA.toInt())
            } else {
                setBackgroundColor(if (idx % 2 == 0) 0xFFEEEEEE.toInt() else 0xFFCCCCCC.toInt())
            }
            setPadding(20, 40, 20, 40)
        }
    }
}

class GameListing : AppCompatActivity() {
    lateinit var dbHandler: MyDBHandler
    var expantion: Boolean = false
    var gameList: ArrayList<Game> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_listing)

        dbHandler = MyDBHandler(this, null, null, 1)
        expantion = intent.getBooleanExtra("expansion", false)

        gameList = if (expantion) {
            dbHandler.getGames(expansion = true)
        } else {
            dbHandler.getGames()
        }

        val tableLayout = TableLayout(this)

        tableLayout.apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT
            )
        }
        tableLayout.addView(GameRow(this, Game(-1, null, null, null), -1, null))
        var on = 1
        for (game in gameList) {
            tableLayout.addView(GameRow(this, game, gameList.indexOf(game), on))
            on++
        }

        findViewById<ScrollView>(R.id.scroll_view).addView(tableLayout)
    }

}