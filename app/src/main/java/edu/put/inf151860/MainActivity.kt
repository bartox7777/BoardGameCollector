package edu.put.inf151860

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    val dbHandler = MyDBHandler(this, null, null, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        print("Hello world")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()

        if(!dbHandler.isAccountAdded()){
            // change activity to Configure
            startActivity(Intent(this, Configure::class.java))
        }
    }
}

class MyDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){
    companion object{
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "bgc.db"
        val TABLE_ACCOUNT = "account"
        val COLUMN_ID = "_id"
        val COLUMN_USERNAME = "username"
        val COLUMN_LASTSYNC = "lastsync"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_ACCOUNT_TABLE = ("CREATE TABLE $TABLE_ACCOUNT($COLUMN_ID INTEGER PRIMARY KEY,$COLUMN_USERNAME TEXT,$COLUMN_LASTSYNC DATETIME)")
        db?.execSQL(CREATE_ACCOUNT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNT")
        onCreate(db)
    }

    public fun isAccountAdded() : Boolean{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ACCOUNT", null)
        if(cursor.count <= 0){
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

    public fun addAccount(username: String){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        db.insert(TABLE_ACCOUNT, null, values)
        db.close()
    }
}