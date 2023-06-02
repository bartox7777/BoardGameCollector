package edu.put.inf151860

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    val dbHandler = MyDBHandler(this, null, null, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("MainActivity", "onCreate")
        dbHandler.deleteAllData()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        Log.i("MainActivity", "onResume")
        super.onResume()

        if (!dbHandler.isAccountAdded()) {
            // change activity to Configure
            Log.i("MainActivity", "isAccountAdded: false")
            startActivity(Intent(this, Configure::class.java))
        } else if (dbHandler.getLastSync() == null) {
            // change activity to Sync
            Log.i("MainActivity", "getLastSync: null")
            startActivity(Intent(this, Sync::class.java))
        }
    }
}

class MyDBHandler(
    context: Context,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "bgc.db"
        val TABLE_ACCOUNT = "account"
        val COLUMN_ID = "_id"
        val COLUMN_USERNAME = "username"
        val COLUMN_LAST_SYNC = "lastsync"
        val COLUMN_LIST_MODIFIED_SINCE_LAST_SYNC = "list_modified_since_last_sync"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_ACCOUNT_TABLE =
            ("CREATE TABLE $TABLE_ACCOUNT($COLUMN_ID INTEGER PRIMARY KEY,$COLUMN_USERNAME TEXT,$COLUMN_LAST_SYNC DATETIME, $COLUMN_LIST_MODIFIED_SINCE_LAST_SYNC INTEGER)")
        db?.execSQL(CREATE_ACCOUNT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNT")
        onCreate(db)
    }

    fun isAccountAdded(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ACCOUNT", null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }


    fun addAccount(username: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_LIST_MODIFIED_SINCE_LAST_SYNC, 0)
        db.insert(TABLE_ACCOUNT, null, values)
        db.close()
    }

    fun getUsername(): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ACCOUNT", null)
        cursor.moveToFirst()
        val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
        cursor.close()
        return username
    }

    fun updateLastSync() {
        val db = this.writableDatabase
        val values = ContentValues()
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateString = dateFormat.format(currentDate)
        values.put(COLUMN_LAST_SYNC, dateString)
        db.update(TABLE_ACCOUNT, values, null, null)
        db.close()
    }

    fun getLastSync(): Date? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ACCOUNT", null)
        cursor.moveToFirst()
        val lastSync = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_SYNC))
        var date: Date? = null
        Log.i("MyDBHandler", "lastSync: $lastSync")
        if (lastSync != null) {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            date = format.parse(lastSync)
        }
        cursor.close()
        return date
    }

    fun getModifiedSinceLastSync(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ACCOUNT", null)
        cursor.moveToFirst()
        val modified =
            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIST_MODIFIED_SINCE_LAST_SYNC))
        cursor.close()
        return modified != 0
    }

    fun setModifiedSinceLastSync(bool: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues()
        val value = if (bool) 1 else 0
        values.put(COLUMN_LIST_MODIFIED_SINCE_LAST_SYNC, value)
        db.update(TABLE_ACCOUNT, values, null, null)
        db.close()
    }

    fun deleteAllData() {
        val db = this.writableDatabase
        db.delete(TABLE_ACCOUNT, null, null)
        db.close()
    }
}