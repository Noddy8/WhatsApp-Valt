package com.noddy.statussaver.views.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.noddy.statussaver.databinding.ItemImagePreviewBinding
import com.noddy.statussaver.databinding.ItemVideoPreviewBinding
import com.noddy.statussaver.models.MediaModel

class SavedMediaPreviewAdapter(
    private val mediaList: List<MediaModel>,
    private val activity: android.app.Activity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_IMAGE = 0
        private const val VIEW_TYPE_VIDEO = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (mediaList[position].type == "video") VIEW_TYPE_VIDEO else VIEW_TYPE_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_VIDEO -> {
                val binding = ItemVideoPreviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                VideoViewHolder(binding)
            }
            else -> {
                val binding = ItemImagePreviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ImageViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val media = mediaList[position]
        when (holder) {
            is VideoViewHolder -> holder.bind(media)
            is ImageViewHolder -> holder.bind(media)
        }
    }

    override fun getItemCount() = mediaList.size

    inner class VideoViewHolder(private val binding: ItemVideoPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var player: ExoPlayer? = null

        fun bind(media: MediaModel) {
            // Hide download tools
            binding.tools.visibility = View.GONE

            // Initialize player
            player = ExoPlayer.Builder(binding.root.context).build()
            binding.playerView.player = player

            // Set media source
            player?.setMediaItem(MediaItem.fromUri(Uri.parse(media.pathUri)))
            player?.prepare()
        }

        fun releasePlayer() {
            player?.release()
            player = null
        }
    }

    inner class ImageViewHolder(private val binding: ItemImagePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(media: MediaModel) {
            // Hide download tools
            binding.tools.visibility = View.GONE

            // Load image
            binding.zoomableImageView.setImageURI(Uri.parse(media.pathUri))
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is VideoViewHolder) {
            holder.releasePlayer()
        }
    }
}