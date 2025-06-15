package com.noddy.statussaver.views.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.noddy.statussaver.R
import com.noddy.statussaver.data.FavoritesRepo
import com.noddy.statussaver.databinding.ItemMediaBinding
import com.noddy.statussaver.models.MediaModel
import com.noddy.statussaver.utils.Constants
import com.noddy.statussaver.views.activities.ImagesPreview
import com.noddy.statussaver.views.activities.VideosPreview

class MediaAdapter(
    var list: ArrayList<MediaModel>,
    val context: Context,
    val showFavoriteOption: Boolean = true
) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    private val favoritesRepo = FavoritesRepo(context)
    var filteredList = ArrayList<MediaModel>()
    private var originalList = ArrayList<MediaModel>()

    init {
        filteredList = ArrayList(list)
        originalList = ArrayList(list)
    }

    inner class ViewHolder(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {
                Glide.with(context)
                    .load(mediaModel.pathUri.toUri())
                    .into(status_image)

                status_play.visibility = if (mediaModel.type == Constants.MEDIA_TYPE_VIDEO)
                    View.VISIBLE else View.GONE

                val downloadImage = if (mediaModel.isDownloaded) R.drawable.ic_downloaded
                else R.drawable.ic_download
                status_download.setImageResource(downloadImage)

                card_status.setOnClickListener {
                    if (mediaModel.type == Constants.MEDIA_TYPE_IMAGE) {
                        context.startActivity(Intent(context, ImagesPreview::class.java).apply {
                            putExtra(Constants.MEDIA_LIST_KEY, list)
                            putExtra(Constants.MEDIA_SCROLL_KEY, adapterPosition)
                        })
                    } else {
                        context.startActivity(Intent(context, VideosPreview::class.java).apply {
                            putExtra(Constants.MEDIA_LIST_KEY, list)
                            putExtra(Constants.MEDIA_SCROLL_KEY, adapterPosition)
                        })
                    }
                }

                status_download.setOnClickListener {
                    val isDownloaded = context.saveStatus(mediaModel)
                    if (isDownloaded) {
                        mediaModel.isDownloaded = true
                        status_download.setImageResource(R.drawable.ic_downloaded)
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Unable to Save", Toast.LENGTH_SHORT).show()
                    }
                }

                // Favorite functionality
                status_favorite.visibility = if (showFavoriteOption) View.VISIBLE else View.GONE
                mediaModel.isFavorite = favoritesRepo.isFavorite(mediaModel)
                val favoriteIcon = if (mediaModel.isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
                status_favorite.setImageResource(favoriteIcon)

                status_favorite.setOnClickListener {
                    mediaModel.isFavorite = !mediaModel.isFavorite
                    val newIcon = if (mediaModel.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_border
                    status_favorite.setImageResource(newIcon)

                    if (mediaModel.isFavorite) favoritesRepo.addFavorite(mediaModel)
                    else favoritesRepo.removeFavorite(mediaModel)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMediaBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount() = filteredList.size

    fun updateList(newList: ArrayList<MediaModel>) {
        list.clear()
        list.addAll(newList)
        filteredList.clear()
        filteredList.addAll(newList)
        originalList.clear()
        originalList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val filtered = ArrayList<MediaModel>()

                if (constraint.isNullOrEmpty()) {
                    filtered.addAll(originalList)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    for (item in originalList) {
                        if (item.fileName.lowercase().contains(filterPattern)) {
                            filtered.add(item)
                        }
                    }
                }

                results.values = filtered
                results.count = filtered.size
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList.clear()
                filteredList.addAll(results?.values as? ArrayList<MediaModel> ?: ArrayList())
                notifyDataSetChanged()
            }
        }
    }
}