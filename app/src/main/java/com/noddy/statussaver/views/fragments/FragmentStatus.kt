package com.noddy.statussaver.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.noddy.statussaver.data.StatusRepo
import com.noddy.statussaver.databinding.FragmentStatusBinding
import com.noddy.statussaver.utils.Constants
import com.noddy.statussaver.viewmodels.factories.StatusViewModel
import com.noddy.statussaver.viewmodels.factories.StatusViewModelFactory
import com.noddy.statussaver.views.adapters.MediaAdapter
import com.noddy.statussaver.views.adapters.MediaViewPagerAdapter

class FragmentStatus : Fragment() {
    private lateinit var binding: FragmentStatusBinding
    private lateinit var type: String
    private lateinit var viewModel: StatusViewModel
    private var currentAdapter: MediaAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentStatusBinding.inflate(layoutInflater)
        arguments?.let {
            val repo = StatusRepo(requireActivity())
            viewModel = ViewModelProvider(
                requireActivity(),
                StatusViewModelFactory(repo)
            )[StatusViewModel::class.java]

            type = it.getString(Constants.FRAGMENT_TYPE_KEY, "")
            setupViewPager()
            setupSearch()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    private fun setupViewPager() {
        val imagesType = when (type) {
            Constants.TYPE_WHATSAPP_MAIN -> Constants.MEDIA_TYPE_WHATSAPP_IMAGES
            else -> Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES
        }

        val videosType = when (type) {
            Constants.TYPE_WHATSAPP_MAIN -> Constants.MEDIA_TYPE_WHATSAPP_VIDEOS
            else -> Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS
        }

        val adapter = MediaViewPagerAdapter(requireActivity(), imagesType, videosType)
        binding.statusViewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.statusViewPager) { tab, pos ->
            tab.text = if (pos == 0) "Images" else "Videos"
        }.attach()
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentAdapter?.filter?.filter(newText)
                return true
            }
        })
    }

    fun setCurrentAdapter(adapter: MediaAdapter) {
        currentAdapter = adapter
    }
}