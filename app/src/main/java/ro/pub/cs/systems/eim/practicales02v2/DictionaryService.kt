package ro.pub.cs.systems.eim.practicales02v2

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class DictionaryService : Service() {

    companion object {
        const val BROADCAST_ACTION = "com.example.dictionaryclient.DICTIONARY_BROADCAST"
        const val EXTRA_DEFINITION = "definition"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val word = intent?.getStringExtra("word")
        if (word != null) {
            fetchDefinition(word)
        }
        return START_NOT_STICKY
    }

    private fun fetchDefinition(word: String) {
        val apiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(apiUrl).build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    val definition = parseDefinition(responseData)
                    sendBroadcast(definition ?: "No definition found.")
                } else {
                    sendBroadcast("Error fetching definition.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                sendBroadcast("An error occurred: ${e.localizedMessage}")
            }
        }
    }

    private fun parseDefinition(jsonResponse: String): String? {
        return try {
            val jsonArray = JSONArray(jsonResponse)
            val firstEntry = jsonArray.getJSONObject(0)
            val meaningsArray = firstEntry.getJSONArray("meanings")
            val firstMeaning = meaningsArray.getJSONObject(0)
            val definitionsArray = firstMeaning.getJSONArray("definitions")
            val firstDefinition = definitionsArray.getJSONObject(0)
            firstDefinition.getString("definition")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun sendBroadcast(definition: String) {
        val intent = Intent(BROADCAST_ACTION)
        intent.putExtra(EXTRA_DEFINITION, definition)
        sendBroadcast(intent)
    }
}