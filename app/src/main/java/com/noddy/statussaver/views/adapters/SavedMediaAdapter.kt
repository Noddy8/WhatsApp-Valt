package com.noddy.statussaver.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.noddy.statussaver.databinding.ItemSavedMediaBinding
import com.noddy.statussaver.models.MediaModel
import com.noddy.statussaver.utils.SavedMediaAction

class SavedMediaAdapter(
    private val list: List<MediaModel>,
    private val context: Context,
    private val onAction: (MediaModel, SavedMediaAction) -> Unit
) : RecyclerView.Adapter<SavedMediaAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSavedMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            Glide.with(context)
                .load(mediaModel.pathUri)
                .into(binding.mediaThumbnail)

            binding.videoPlayIcon.visibility =
                if (mediaModel.type == "video") View.VISIBLE else View.GONE

            binding.root.setOnClickListener { onAction(mediaModel, SavedMediaAction.VIEW) }
            binding.deleteButton.setOnClickListener {
                onAction(
                    mediaModel,
                    SavedMediaAction.DELETE
                )
            }
            binding.shareButton.setOnClickListener { onAction(mediaModel, SavedMediaAction.SHARE) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSavedMediaBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}