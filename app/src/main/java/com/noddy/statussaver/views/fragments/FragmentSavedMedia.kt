package com.noddy.statussaver.views.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.noddy.statussaver.R
import com.noddy.statussaver.databinding.FragmentSavedMediaBinding
import com.noddy.statussaver.databinding.FragmentSavedMediaListBinding
import com.noddy.statussaver.models.MediaModel
import com.noddy.statussaver.utils.Constants
import com.noddy.statussaver.utils.SavedMediaAction
import com.noddy.statussaver.views.activities.SavedMediaPreviewActivity
import com.noddy.statussaver.views.adapters.SavedMediaAdapter
import java.io.File

class FragmentSavedMedia : Fragment() {
    private lateinit var binding: FragmentSavedMediaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSavedMediaBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = SavedMediaPagerAdapter(requireActivity())
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Images"
                1 -> "Videos"
                else -> null
            }
        }.attach()
    }

    inner class SavedMediaPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SavedMediaListFragment.newInstance("image")
                1 -> SavedMediaListFragment.newInstance("video")
                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }

    class SavedMediaListFragment : Fragment() {
        private var _binding: FragmentSavedMediaListBinding? = null
        private val binding get() = _binding!!
        private lateinit var mediaType: String
        private val savedMediaList = ArrayList<MediaModel>()

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ) = FragmentSavedMediaListBinding.inflate(inflater, container, false).also {
            _binding = it
        }.root

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            arguments?.getString(MEDIA_TYPE_KEY)?.let { type ->
                mediaType = type
            }
            setupRecyclerView()
            setupSwipeRefresh()
            loadSavedMedia()
        }

        private fun setupRecyclerView() {
            binding.savedMediaRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            binding.savedMediaRecyclerView.adapter = SavedMediaAdapter(savedMediaList, requireContext()) { media, action, position ->
                when (action) {
                    SavedMediaAction.VIEW -> viewMedia(media, position)
                    SavedMediaAction.DELETE -> deleteMedia(media)
                }
            }
        }

        private fun setupSwipeRefresh() {
            binding.swipeRefreshLayout.setOnRefreshListener {
                loadSavedMedia()
            }
        }

        private fun loadSavedMedia() {
            savedMediaList.clear()
            val saveDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                requireContext().getString(R.string.app_name)
            )

            if (saveDir.exists() && saveDir.isDirectory) {
                saveDir.listFiles()?.forEach { file ->
                    val fileType = if (file.extension.equals("mp4", ignoreCase = true)) "video" else "image"
                    if (fileType == mediaType) {
                        savedMediaList.add(
                            MediaModel(
                                pathUri = file.absolutePath,
                                fileName = file.name,
                                type = fileType,
                                isDownloaded = true
                            )
                        )
                    }
                }
            }
            binding.savedMediaRecyclerView.adapter?.notifyDataSetChanged()
            binding.emptyText.visibility = if (savedMediaList.isEmpty()) View.VISIBLE else View.GONE
            binding.swipeRefreshLayout.isRefreshing = false
        }

        private fun viewMedia(media: MediaModel, position: Int) {
            val intent = Intent(requireContext(), SavedMediaPreviewActivity::class.java).apply {
                putExtra(Constants.MEDIA_LIST_KEY, ArrayList(savedMediaList))
                putExtra(Constants.MEDIA_SCROLL_KEY, position)
            }
            startActivity(intent)
        }

        private fun deleteMedia(media: MediaModel) {
            File(media.pathUri).delete()
            loadSavedMedia()
        }

        private fun shareMedia(media: MediaModel) {
            val file = File(media.pathUri)
            if (!file.exists()) {
                Toast.makeText(requireContext(), "File not found", Toast.LENGTH_SHORT).show()
                return
            }

            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = if (media.type == "video") "video/*" else "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Grant temporary read permission to the content URI
            val resInfoList = requireContext().packageManager
                .queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                requireContext().grantUriPermission(
                    packageName,
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        companion object {
            private const val MEDIA_TYPE_KEY = "MEDIA_TYPE"

            fun newInstance(mediaType: String): SavedMediaListFragment {
                return SavedMediaListFragment().apply {
                    arguments = Bundle().apply {
                        putString(MEDIA_TYPE_KEY, mediaType)
                    }
                }
            }
        }
    }
}