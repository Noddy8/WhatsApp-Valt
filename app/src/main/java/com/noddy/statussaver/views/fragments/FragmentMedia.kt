package com.noddy.statussaver.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.noddy.statussaver.data.StatusRepo
import com.noddy.statussaver.databinding.FragmentMediaBinding
import com.noddy.statussaver.models.MediaModel
import com.noddy.statussaver.utils.Constants
import com.noddy.statussaver.utils.SharedPrefKeys
import com.noddy.statussaver.utils.SharedPrefUtils
import com.noddy.statussaver.utils.SortingUtils
import com.noddy.statussaver.viewmodels.factories.StatusViewModel
import com.noddy.statussaver.viewmodels.factories.StatusViewModelFactory
import com.noddy.statussaver.views.adapters.MediaAdapter

class FragmentMedia : Fragment() {
    private val binding by lazy { FragmentMediaBinding.inflate(layoutInflater) }
    private lateinit var viewModel: StatusViewModel
    private lateinit var adapter: MediaAdapter
    private var mediaType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val repo = StatusRepo(requireActivity())
            viewModel = ViewModelProvider(
                requireActivity(),
                StatusViewModelFactory(repo)
            )[StatusViewModel::class.java]

            mediaType = it.getString(Constants.MEDIA_TYPE_KEY, "")
            setupObserver()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    private fun setupObserver() {
        when (mediaType) {
            Constants.MEDIA_TYPE_WHATSAPP_IMAGES -> {
                viewModel.whatsAppImagesLiveData.observe(requireActivity()) { list ->
                    applySorting(list)
                }
            }
            Constants.MEDIA_TYPE_WHATSAPP_VIDEOS -> {
                viewModel.whatsAppVideosLiveData.observe(requireActivity()) { list ->
                    applySorting(list)
                }
            }
            Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES -> {
                viewModel.whatsAppBusinessImagesLiveData.observe(requireActivity()) { list ->
                    applySorting(list)
                }
            }
            Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS -> {
                viewModel.whatsAppBusinessVideosLiveData.observe(requireActivity()) { list ->
                    applySorting(list)
                }
            }
        }
    }

    private fun applySorting(unFilteredList: ArrayList<MediaModel>) {
        val sortType = SharedPrefUtils.getPrefInt(SharedPrefKeys.PREF_SORT_TYPE, SortingUtils.SORT_DATE_DESC)
        val sortedList = SortingUtils.sortMediaList(unFilteredList.distinctBy { it.fileName } as ArrayList, sortType)

        if (::adapter.isInitialized) {
            adapter.updateList(sortedList)
        } else {
            adapter = MediaAdapter(sortedList, requireActivity())
            binding.mediaRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            binding.mediaRecyclerView.adapter = adapter
        }

        binding.tempMediaText.visibility = if (sortedList.isEmpty()) View.VISIBLE else View.GONE
    }
}