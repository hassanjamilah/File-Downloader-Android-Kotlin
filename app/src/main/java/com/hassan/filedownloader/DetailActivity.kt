package com.hassan.filedownloader

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repoName = intent.getStringExtra("repo_name") ?: "Unknown"
        val status = intent.getStringExtra("status") ?: "N/A"

        findViewById<TextView>(R.id.repoNameText).text = repoName
        findViewById<TextView>(R.id.statusText).text = "Status: $status"

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
}