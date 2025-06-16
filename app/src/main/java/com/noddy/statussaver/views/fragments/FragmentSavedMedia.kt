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
import androidx.recyclerview.widget.GridLayoutManager
import com.noddy.statussaver.R
import com.noddy.statussaver.databinding.FragmentSavedMediaBinding
import com.noddy.statussaver.models.MediaModel
import com.noddy.statussaver.utils.Constants
import com.noddy.statussaver.utils.SavedMediaAction
import com.noddy.statussaver.views.activities.ImagesPreview
import com.noddy.statussaver.views.activities.VideosPreview
import com.noddy.statussaver.views.adapters.SavedMediaAdapter
import java.io.File

class FragmentSavedMedia : Fragment() {
    private lateinit var binding: FragmentSavedMediaBinding
    private lateinit var adapter: SavedMediaAdapter
    private val savedMediaList = ArrayList<MediaModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSavedMediaBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        loadSavedMedia()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadSavedMedia()
        }
    }

    private fun setupRecyclerView() {
        adapter = SavedMediaAdapter(savedMediaList, requireContext()) { media, action ->
            when (action) {
                SavedMediaAction.VIEW -> viewMedia(media)
                SavedMediaAction.DELETE -> deleteMedia(media)
                SavedMediaAction.SHARE -> shareMedia(media)
            }
        }
        binding.savedMediaRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.savedMediaRecyclerView.adapter = adapter
    }

    private fun loadSavedMedia() {
        savedMediaList.clear()
        val saveDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            requireContext().getString(R.string.app_name)
        )

        if (saveDir.exists() && saveDir.isDirectory) {
            saveDir.listFiles()?.forEach { file ->
                val type = if (file.extension == "mp4") "video" else "image"
                savedMediaList.add(
                    MediaModel(
                        pathUri = file.absolutePath,
                        fileName = file.name,
                        type = type,
                        isDownloaded = true
                    )
                )
            }
        }
        adapter.notifyDataSetChanged()
        binding.emptyText.visibility = if (savedMediaList.isEmpty()) View.VISIBLE else View.GONE

        // Stop refresh animation
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun viewMedia(media: MediaModel) {
        val intent = if (media.type == "video") {
            Intent(requireContext(), VideosPreview::class.java).apply {
                val list = ArrayList<MediaModel>()
                list.add(media)
                putExtra(Constants.MEDIA_LIST_KEY, list)
                putExtra(Constants.MEDIA_SCROLL_KEY, 0)
            }
        } else {
            Intent(requireContext(), ImagesPreview::class.java).apply {
                val list = ArrayList<MediaModel>()
                list.add(media)
                putExtra(Constants.MEDIA_LIST_KEY, list)
                putExtra(Constants.MEDIA_SCROLL_KEY, 0)
            }
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
}