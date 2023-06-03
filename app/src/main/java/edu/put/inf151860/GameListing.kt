package edu.put.inf151860

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class Game(id: Long, title: String, year: Int, thumbnailUrl: String) {
    val ID: Int? = null
    val name: String? = null
    val year: Int? = null
    val thumbnailURL: String? = null
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

        if (expantion) {
            gameList = dbHandler.getGames(expansion=true)
        } else {
            gameList = dbHandler.getGames()
        }

        Log.i("GameListing", "gameList: ${gameList.count()}: $gameList")
    }


}