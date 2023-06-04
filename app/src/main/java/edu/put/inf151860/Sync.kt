package edu.put.inf151860

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Thread.sleep
import java.net.URL
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
            findViewById<TextView>(R.id.progress_text).visibility = TextView.VISIBLE
            findViewById<ProgressBar>(R.id.progress_bar).visibility = ProgressBar.VISIBLE
            sync()
        }

        // listener for sync expansions button
        findViewById<Button>(R.id.sync_expansions).setOnClickListener() {
            findViewById<TextView>(R.id.progress_text).visibility = TextView.VISIBLE
            findViewById<ProgressBar>(R.id.progress_bar).visibility = ProgressBar.VISIBLE
            sync(1)
        }

        findViewById<Button>(R.id.mainScreen_button).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.eraseData2).setOnClickListener {
            dbHandler.deleteAccountData()
            finishAffinity()
        }
    }

    private fun sync(extension: Int = 0) {
        // download XML file asynchronously
        fun downloadFile() {
            var urlString: String
            if (extension == 0) urlString =
                "https://boardgamegeek.com/xmlapi2/collection?username=${dbHandler.getUsername()}"
            else {
                urlString =
                    "https://boardgamegeek.com/xmlapi2/collection?username=${dbHandler.getUsername()}&subtype=boardgameexpansion"
            }
            Log.i("downloadFile", urlString)
            val xmlDirectory = File(filesDir, "xml")
            if (!xmlDirectory.exists()) {
                xmlDirectory.mkdir()
            }
            if (xmlDirectory.listFiles() != null) {
                for (file in xmlDirectory.listFiles()) {
                    file.delete()
                }
            }
            val xmlFile = File(xmlDirectory, "collection.xml")
            Log.i("downloadFile", xmlFile.toString())

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val url = URL(urlString)
                    val connection = url.openConnection()
                    connection.connect()
                    val input = BufferedInputStream(url.openStream())
                    val output = FileOutputStream(xmlFile)
                    val data = ByteArray(1024)
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        output.write(data, 0, count)
                    }
                    output.flush()
                    output.close()
                    input.close()

                    // if specific string in XML file is found, it means that the file is incomplete
                    if (xmlFile.readText().contains("Please try again later for access.")) {
                        throw Exception("Dane na serwerze są przygotowywane. Spróbuj ponownie za kilka sekund.")
                    }
                    if (xmlFile.readText().contains("Invalid username specified")) {
                        throw Exception("Taki użytkownik nie istnieje w bazie BGC.")
                    }
                    if (xmlFile.readText().contains("<error>")) {
                        throw Exception("Wystąpił nieznany błąd. Spróbuj ponownie później.")
                    }

                    withContext(Dispatchers.Main) {
                        delay(500)
                        dbHandler.updateLastSync()
                        updateLastSyncText()
                        dbHandler.setModifiedSinceLastSync(false)
//                        dbHandler.setModifiedSinceLastSync(true)
//                        Toast.makeText(this@Sync, "Synchronizacja zakończona", Toast.LENGTH_SHORT)
//                            .show()

                        val xmlDirectory = File(filesDir, "xml")
                        val file = File(xmlDirectory, "collection.xml")
                        if (file.exists()) {
                            if (extension == 0) {
                                dbHandler.deleteCollectionData()
                            }
                            // parse XML file

                            val factory = XmlPullParserFactory.newInstance()
                            factory.isNamespaceAware = true
                            val parser = factory.newPullParser()
//
                            parser.setInput(file.inputStream(), null)
//
                            var objectID: Long? = null
                            var name: String? = null
                            var yearPublished: Int? = null
                            var thumbnail: String? = null
                            var subtype: String? = null

                            var eventType = parser.eventType
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                when (eventType) {
                                    XmlPullParser.START_TAG -> {
                                        val tagName = parser.name
                                        when (tagName) {
                                            "item" -> {
//                                    val objectType = parser.getAttributeValue(null, "objecttype")
                                                objectID =
                                                    parser.getAttributeValue(null, "objectid")
                                                        .toLong()
                                                subtype = parser.getAttributeValue(null, "subtype")
//                                    val collid = parser.getAttributeValue(null, "collid")
                                            }

                                            "name" -> {
                                                name = parser.nextText()
//                                    val sortIndex = parser.getAttributeValue(null, "sortindex")
                                            }

                                            "yearpublished" -> {
                                                yearPublished = parser.nextText().toInt()
                                            }

                                            "image" -> {
//                                    val image = parser.nextText()
                                            }

                                            "thumbnail" -> {
                                                thumbnail = parser.nextText()
                                            }

                                            "status" -> {
//                                    val own = parser.getAttributeValue(null, "own")
//                                    val prevOwned = parser.getAttributeValue(null, "prevowned")
//                                    val forTrade = parser.getAttributeValue(null, "fortrade")
//                                    val want = parser.getAttributeValue(null, "want")
//                                    val wantToPlay = parser.getAttributeValue(null, "wanttoplay")
//                                    val wantToBuy = parser.getAttributeValue(null, "wanttobuy")
//                                    val wishlist = parser.getAttributeValue(null, "wishlist")
//                                    val preordered = parser.getAttributeValue(null, "preordered")
//                                    val lastModified =
//                                        parser.getAttributeValue(null, "lastmodified")
                                            }

                                            "numplays" -> {
//                                    val numPlays = parser.nextText()
                                            }
                                        }
                                    }

                                    XmlPullParser.END_TAG -> {
                                        val tagName = parser.name
                                        when (tagName) {
                                            "item" -> {
                                                if (extension == 0 && objectID != null) dbHandler.saveGame(
                                                    objectID, name, yearPublished, thumbnail
                                                )
                                                else if (extension == 1 && objectID != null) dbHandler.makeExpansion(
                                                    objectID
                                                )
                                                objectID = null
                                                name = null
                                                yearPublished = null
                                                thumbnail = null
                                            }
                                        }
                                    }
                                }
                                eventType = parser.next()
//                                Log.i("next", "")
                            }

                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Sync, "$e", Toast.LENGTH_SHORT
                        ).show()
                    }
                    findViewById<TextView>(R.id.progress_text).visibility = TextView.INVISIBLE
                    findViewById<ProgressBar>(R.id.progress_bar).visibility = ProgressBar.INVISIBLE
                    Log.e("downloadFile", e.toString())
                    val incompleteFile = File(xmlDirectory, "collection.xml")
                    if (incompleteFile.exists()) {
                        incompleteFile.delete()
                    }
                }
            }
        }

        fun properSync() {
            downloadFile()

        }

        val lastSync = dbHandler.getLastSync()
        if (lastSync != null) {
            if (dbHandler.getModifiedSinceLastSync() || extension == 1) {
                // if different between current date and last sync is more than 1 day
                if (ChronoUnit.DAYS.between(lastSync.toInstant(), Date().toInstant()) >= 1) {
                    properSync()
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Potwierdzenie synchronizacji")
                    if (extension == 0) builder.setMessage("Synchronizację wykonano mniej niż 24h temu. Czy na pewno chcesz ją wykonać?")
                    else builder.setMessage("Upewnij się, że wcześniej wykonałeś standardową synchronizację.")

                    builder.setPositiveButton("OK") { dialog, _ ->
                        properSync()
                        dialog.dismiss()
                    }

                    builder.setNegativeButton("ANULUJ") { dialog, _ ->
                        Toast.makeText(this, "Anulowano synchronizację", Toast.LENGTH_SHORT).show()
                        findViewById<TextView>(R.id.progress_text).visibility = TextView.INVISIBLE
                        findViewById<ProgressBar>(R.id.progress_bar).visibility =
                            ProgressBar.INVISIBLE
                        dialog.dismiss()
                    }

                    val alert = builder.create()
                    alert.show()
                }
            } else {
                Toast.makeText(this, "Brak zmian do synchronizacji", Toast.LENGTH_SHORT).show()
                findViewById<TextView>(R.id.progress_text).visibility = TextView.INVISIBLE
                findViewById<ProgressBar>(R.id.progress_bar).visibility = ProgressBar.INVISIBLE
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
        findViewById<TextView>(R.id.progress_text).visibility = TextView.INVISIBLE
        findViewById<ProgressBar>(R.id.progress_bar).visibility = ProgressBar.INVISIBLE
    }
}