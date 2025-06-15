package com.noddy.statussaver.views.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.noddy.statussaver.R
import com.noddy.statussaver.databinding.ItemImagePreviewBinding
import com.noddy.statussaver.models.MEDIA_TYPE_IMAGE
import com.noddy.statussaver.models.MediaModel
import com.noddy.statussaver.utils.saveStatus
import java.io.File

class ImagePreviewAdapter (val list: ArrayList<MediaModel>, val context: Context) :
    RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemImagePreviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {
                Glide.with(context)
                    .load(mediaModel.pathUri.toUri())
                    .into(zoomableImageView)

                val downloadImage = if (mediaModel.isDownloaded) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.ic_download
                }
                tools.statusDownload.setImageResource(downloadImage)

                // Share functionality
                tools.linearLayout.setOnClickListener {
                    shareMedia(mediaModel)
                }



                // Download functionality
                tools.download.setOnClickListener {
                    val isDownloaded = context.saveStatus(mediaModel)
                    if (isDownloaded) {
                        // status is downloaded
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        mediaModel.isDownloaded = true
                        tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
                    } else {
                        // unable to download status
                        Toast.makeText(context, "Unable to Save", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun shareMedia(mediaModel: MediaModel) {
            try {
                // Handle different URI formats
                val uri = when {
                    mediaModel.pathUri.startsWith("content://") -> {
                        // Already a content URI
                        Uri.parse(mediaModel.pathUri)
                    }
                    mediaModel.pathUri.startsWith("file://") -> {
                        // File URI - convert to File and then to content URI
                        val file = File(Uri.parse(mediaModel.pathUri).path ?: mediaModel.pathUri)
                        if (file.exists()) {
                            FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                        } else {
                            Toast.makeText(context, "File not found at: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                            return
                        }
                    }
                    else -> {
                        // Plain file path
                        val file = File(mediaModel.pathUri)
                        if (file.exists()) {
                            FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                        } else {
                            Toast.makeText(context, "File not found at: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                            return
                        }
                    }
                }

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = if (mediaModel.type == MEDIA_TYPE_IMAGE) "image/*" else "video/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(shareIntent, "Share via")
                context.startActivity(chooser)

            } catch (e: Exception) {
                Toast.makeText(context, "Unable to share: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagePreviewAdapter.ViewHolder {
        return ViewHolder(ItemImagePreviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ImagePreviewAdapter.ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size
}