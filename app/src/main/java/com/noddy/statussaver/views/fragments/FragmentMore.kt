package com.noddy.statussaver.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.noddy.statussaver.databinding.FragmentMoreBinding
import com.noddy.statussaver.views.activities.BusinessStatusActivity
import com.noddy.statussaver.views.activities.SettingsActivity

class FragmentMore : Fragment() {
    private lateinit var binding: FragmentMoreBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.businessOption.setOnClickListener {
            startActivity(Intent(requireContext(), BusinessStatusActivity::class.java))
        }

        binding.settingsOption.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
    }
}