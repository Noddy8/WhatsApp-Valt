package com.noddy.statussaver.views.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.noddy.statussaver.databinding.ActivityDirectMessageBinding
import java.net.URLEncoder

class DirectMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDirectMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBar.setNavigationOnClickListener { finish() }

        // Send WhatsApp message
        binding.sendButton.setOnClickListener {
            val phoneNumber = binding.phoneInput.text.toString().trim()
            val message = binding.messageInput.text.toString().trim()

            if (phoneNumber.isNotEmpty()) {
                val encodedMessage = URLEncoder.encode(message, "UTF-8")
                val url = if (message.isNotEmpty()) {
                    "https://wa.me/$phoneNumber?text=$encodedMessage"
                } else {
                    "https://wa.me/$phoneNumber"
                }

                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch (e: Exception) {
                    Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.phoneInput.error = "Please enter a phone number"
            }
        }

        // Add to contacts
        binding.addContactButton.setOnClickListener {
            val phoneNumber = binding.phoneInput.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                addToContacts(phoneNumber)
            } else {
                binding.phoneInput.error = "Please enter a phone number"
            }
        }
    }

    private fun addToContacts(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to manual contact creation
            val contactUri = Uri.parse("tel:$phoneNumber")
            val intentFallback = Intent(Intent.ACTION_INSERT_OR_EDIT).apply {
                type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
                data = contactUri
            }
            startActivity(intentFallback)
        }
    }
}