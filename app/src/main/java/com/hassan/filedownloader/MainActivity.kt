package com.hassan.filedownloader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val button = findViewById<LoadingButton>(R.id.loadingButton)
        button.setOnClickListener {
            val selectedUrl = getSelectedUrl()
            if (selectedUrl == null) {
                Toast.makeText(this, "Please select a repository to download", Toast.LENGTH_SHORT).show()
            } else {
                button.startLoading()
                download(selectedUrl) {
                    runOnUiThread {
                        button.completeLoading()
                        showNotification(selectedUrl) // coming up next step
                    }
                }
            }
        }
    }

    private fun getSelectedUrl(): String? {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val selectedId = radioGroup.checkedRadioButtonId
        if (selectedId == -1) return null

        val selectedRadio = findViewById<RadioButton>(selectedId)
        return selectedRadio.tag as String
    }

    fun download(url: String, onComplete: () -> Unit) {
        Thread {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                val file = File(cacheDir, "download.zip")
                val input = connection.inputStream
                val output = FileOutputStream(file)

                input.copyTo(output)

                input.close()
                output.close()
                connection.disconnect()

                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    fun showNotification(url: String) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("repo_name", getRepoName(url))
            putExtra("status", "Success")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "channelId")
            .setSmallIcon(R.drawable.ic_download) // add icon in drawable
            .setContentTitle("Download Complete")
            .setContentText("Tap to view details")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(R.drawable.ic_details, "View Details", pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(1001, builder.build())
        }
    }

    private fun getRepoName(url: String): String {
        return url.substringAfterLast('/')
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "DownloadChannel"
            val descriptionText = "Shows download status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channelId", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}