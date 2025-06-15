package com.noddy.statussaver.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.noddy.statussaver.R
import com.noddy.statussaver.databinding.FragmentSettingsBinding
import com.noddy.statussaver.models.SettingsModel
import com.noddy.statussaver.utils.AutoCleanScheduler
import com.noddy.statussaver.utils.SharedPrefKeys
import com.noddy.statussaver.utils.SharedPrefUtils
import com.noddy.statussaver.views.adapters.SettingsAdapter

class FragmentSettings : Fragment() {
    private val binding by lazy { FragmentSettingsBinding.inflate(layoutInflater) }
    private val list = ArrayList<SettingsModel>()
    private val adapter by lazy { SettingsAdapter(list, requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.settingsRecyclerView.adapter = adapter

        list.add(SettingsModel("How to use", "Know how to download statuses"))
        list.add(SettingsModel("Save in Folder", "/Documents/${getString(R.string.app_name)}"))
        list.add(SettingsModel("Auto Clean", "Automatically delete old statuses"))
        list.add(SettingsModel("Disclaimer", "Read Our Disclaimer"))
        list.add(SettingsModel("Privacy Policy", "Read Our Terms & Conditions"))
        list.add(SettingsModel("Share", "Sharing is caring"))
        list.add(SettingsModel("Rate Us", "Please support our work by rating on PlayStore"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root
}