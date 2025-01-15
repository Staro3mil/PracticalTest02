package ro.pub.cs.systems.eim.practicales02v2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var etWord: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvDefinition: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v2_main)

        // Initialize UI elements
        etWord = findViewById(R.id.etWord)
        btnSearch = findViewById(R.id.btnSearch)
        tvDefinition = findViewById(R.id.tvDefinition)

        // Set button click listener
        btnSearch.setOnClickListener {
            val word = etWord.text.toString().trim()
            if (word.isNotEmpty()) {
                fetchDefinition(word)
            } else {
                tvDefinition.text = "Please enter a word."
                Log.d("DictionaryApp", "Please enter a word.")
            }
        }
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
                    runOnUiThread {
                        tvDefinition.text = definition ?: "No definition found."
                    }
                    Log.d("DictionaryApp", "Definition for '$word': ${definition ?: "Not found"}")
                } else {
                    runOnUiThread {
                        tvDefinition.text = "Error fetching definition."
                    }
                    Log.d("DictionaryApp", "Error fetching definition for '$word'")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    tvDefinition.text = "An error occurred: ${e.localizedMessage}"
                }
                Log.e("DictionaryApp", "An error occurred: ${e.localizedMessage}")
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
            Log.e("DictionaryApp", "Error parsing JSON response: ${e.localizedMessage}")
            null
        }
    }
}
