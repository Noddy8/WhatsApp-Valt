package com.noddy.statussaver.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.noddy.statussaver.R
import com.noddy.statussaver.databinding.ActivitySettingsBinding
import com.noddy.statussaver.models.SettingsModel
import com.noddy.statussaver.views.adapters.SettingsAdapter

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val settingsList = ArrayList<SettingsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        binding.toolBar.setNavigationOnClickListener { finish() }

        // Populate settings list
        settingsList.add(SettingsModel("How to use", "Know how to download statuses"))
        settingsList.add(SettingsModel("Save in Folder", "/internalstorage/Documents/Status Saver"))
        settingsList.add(SettingsModel("Disclaimer", "Read Our Disclaimer"))
        settingsList.add(SettingsModel("Privacy Policy", "Read Our Terms & Conditions"))
        settingsList.add(SettingsModel("Share", "Sharing is caring"))
        settingsList.add(SettingsModel("Rate Us", "Please support our work by rating on PlayStore"))

        // Set up RecyclerView
        binding.settingsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.settingsRecyclerView.adapter = SettingsAdapter(settingsList, this)
    }
}