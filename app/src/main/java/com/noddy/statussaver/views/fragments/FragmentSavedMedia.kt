package com.noddy.statussaver.views.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.noddy.statussaver.R
import com.noddy.statussaver.databinding.FragmentSavedMediaBinding
import com.noddy.statussaver.models.MediaModel
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSavedMediaBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadSavedMedia()
    }

    private fun setupRecyclerView() {
        adapter = SavedMediaAdapter(savedMediaList, requireContext()) { media, action: Enum<SavedMediaAction> ->
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
    }

    private fun viewMedia(media: MediaModel) {
        val intent = if (media.type == "video") {
            Intent(requireContext(), VideosPreview::class.java)
        } else {
            Intent(requireContext(), ImagesPreview::class.java)
        }
        intent.putExtra("MEDIA_PATH", media.pathUri)
        startActivity(intent)
    }

    private fun deleteMedia(media: MediaModel) {
        File(media.pathUri).delete()
        loadSavedMedia()
    }

    private fun shareMedia(media: MediaModel) {
        val file = File(media.pathUri)
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (media.type == "video") "video/*" else "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }
}

