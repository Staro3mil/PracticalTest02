package ro.pub.cs.systems.eim.practicales02v2

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import kotlin.concurrent.thread

class ServerTimeActivity : AppCompatActivity() {

    private lateinit var serverTimeView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.server)

        serverTimeView = findViewById(R.id.serverTimeView)

        // Pornim thread-ul de conectare
        thread { connectToServer("192.168.56.1", 12345) } // Adaptează IP-ul serverului
    }

    private fun connectToServer(host: String, port: Int) {
        try {
            val socket = Socket(host, port)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            while (true) {
                val serverResponse = reader.readLine()
                runOnUiThread {
                    serverTimeView.text = serverResponse
                }
            }
        } catch (e: Exception) {
            Log.e("ServerConnectionError", e.message ?: "Unknown error")
        }
    }
}
