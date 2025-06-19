package com.noddy.statussaver.views.fragments

import com.bumptech.glide.Glide
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noddy.statussaver.R
import com.noddy.statussaver.databinding.FragmentSavedMediaListBinding
import com.noddy.statussaver.databinding.ItemSavedMediaBinding
import com.noddy.statussaver.models.MediaModel
import com.noddy.statussaver.utils.Constants
import com.noddy.statussaver.utils.SavedMediaAction
import com.noddy.statussaver.views.activities.SavedMediaPreviewActivity
import java.io.File


class FragmentSavedMediaList : Fragment() {
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

    inner class SavedMediaAdapter(
        private val list: List<MediaModel>,
        context: Context,
        private val onAction: (MediaModel, SavedMediaAction, Int) -> Unit
    ) : RecyclerView.Adapter<SavedMediaAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemSavedMediaBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(mediaModel: MediaModel, position: Int) {
                context?.let {
                    Glide.with(it)
                        .load(mediaModel.pathUri)
                        .into(binding.mediaThumbnail)
                }

                binding.videoPlayIcon.visibility =
                    if (mediaModel.type == "video") View.VISIBLE else View.GONE

                binding.root.setOnClickListener {
                    onAction(mediaModel, SavedMediaAction.VIEW, position)
                }
                binding.deleteButton.setOnClickListener {
                    onAction(mediaModel, SavedMediaAction.DELETE, position)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemSavedMediaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(list[position], position)
        }

        override fun getItemCount() = list.size
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MEDIA_TYPE_KEY = "MEDIA_TYPE"

        fun newInstance(mediaType: String): FragmentSavedMedia.SavedMediaListFragment {
            return FragmentSavedMedia.SavedMediaListFragment().apply {
                arguments = Bundle().apply {
                    putString(MEDIA_TYPE_KEY, mediaType)
                }
            }
        }
    }
}