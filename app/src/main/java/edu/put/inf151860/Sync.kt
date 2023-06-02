package edu.put.inf151860

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.Date

class Sync : AppCompatActivity() {
    lateinit var dbHandler: MyDBHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync2)

        dbHandler = MyDBHandler(this, null, null, 1)
        updateLastSyncText()

        // listener for sync button
        findViewById<Button>(R.id.sync_button).setOnClickListener {
            sync()
        }

        findViewById<Button>(R.id.mainScreen_button).setOnClickListener {
            finish()
        }
    }

    private fun sync() {
        fun properSync() {
            dbHandler.updateLastSync()
            updateLastSyncText()
            dbHandler.setModifiedSinceLastSync(false)
            Toast.makeText(this, "Synchronizacja zakończona", Toast.LENGTH_SHORT).show()
        }

        val lastSync = dbHandler.getLastSync()
        if (lastSync != null) {
            if (dbHandler.getModifiedSinceLastSync()) {
                // if different between current date and last sync is more than 1 day
                if (ChronoUnit.DAYS.between(lastSync.toInstant(), Date().toInstant()) >= 1) {
                    properSync()
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Potwierdzenie synchronizacji")
                    builder.setMessage("Synchronizację wykonano mniej niż 24h temu. Czy na pewno chcesz ją wykonać?")

                    builder.setPositiveButton("TAK") { dialog, _ ->
                        properSync()
                        dialog.dismiss()
                    }

                    builder.setNegativeButton("NIE") { dialog, _ ->
                        Toast.makeText(this, "Anulowano synchronizację", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                    val alert = builder.create()
                    alert.show()
                }
            } else{
                Toast.makeText(this, "Brak zmian do synchronizacji", Toast.LENGTH_SHORT).show()
            }
        } else {
            properSync()
        }
    }

    private fun updateLastSyncText() {
        val lastSync = dbHandler.getLastSync()
        if (lastSync != null) {
            val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
            findViewById<TextView>(R.id.last_sync).text = formatter.format(lastSync)
        } else {
            findViewById<TextView>(R.id.last_sync).text = "Brak (synchronizacja wymagana)"
        }
    }
}