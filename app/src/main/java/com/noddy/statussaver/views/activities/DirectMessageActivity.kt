package com.noddy.statussaver.views.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.noddy.statussaver.databinding.ActivityDirectMessageBinding

class DirectMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDirectMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBar.setNavigationOnClickListener { finish() }

        binding.sendButton.setOnClickListener {
            val phoneNumber = binding.phoneInput.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$phoneNumber")
                }
                startActivity(intent)
            } else {
                binding.phoneInput.error = "Please enter a phone number"
            }
        }
    }
}