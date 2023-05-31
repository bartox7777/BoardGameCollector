package edu.put.inf151860

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Configure : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configure)
        findViewById<Button>(R.id.button_saveAccount).setOnClickListener{
            saveAccount()
        }
    }

    public fun saveAccount(){
        val dbHandler = MyDBHandler(this, null, null, 1)
        val username = findViewById<EditText>(R.id.editText_username).text.toString()
        if (username != ""){
            dbHandler.addAccount(username)
            // wyświetl komunikat o sukcesie i zamknij aktywność
            Toast.makeText(this, "Zapisano konto", Toast.LENGTH_SHORT).show()
            finish()
        } else{
            // wyświetl komunikat o błędzie
            Toast.makeText(this, "Nie podano nazwy użytkownika", Toast.LENGTH_SHORT).show()
        }
    }
}