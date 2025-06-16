package com.noddy.statussaver.views.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.noddy.statussaver.R
import com.noddy.statussaver.databinding.ActivitySavedMediaPreviewBinding
import com.noddy.statussaver.models.MediaModel
import com.noddy.statussaver.utils.Constants
import com.noddy.statussaver.views.adapters.SavedMediaPreviewAdapter
import java.io.File

class SavedMediaPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedMediaPreviewBinding
    private lateinit var mediaList: ArrayList<MediaModel>
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedMediaPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mediaList = intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as ArrayList<MediaModel>
        currentPosition = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)

        setupViewPager()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = SavedMediaPreviewAdapter(mediaList, this)
        binding.viewPager.setCurrentItem(currentPosition, false)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                supportActionBar?.title = mediaList[position].fileName
            }
        })

        supportActionBar?.title = mediaList[currentPosition].fileName
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_saved_media_preview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_share -> {
                shareMedia()
                true
            }
            R.id.action_delete -> {
                deleteMedia()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteMedia() {
        val media = mediaList[currentPosition]
        val file = File(media.pathUri)
        if (file.exists() && file.delete()) {
            mediaList.removeAt(currentPosition)
            binding.viewPager.adapter?.notifyDataSetChanged()

            if (mediaList.isEmpty()) {
                finish()
            } else {
                // Adjust position after deletion
                if (currentPosition >= mediaList.size) {
                    currentPosition = mediaList.size - 1
                }
                binding.viewPager.setCurrentItem(currentPosition, false)
                supportActionBar?.title = mediaList[currentPosition].fileName
            }
            Toast.makeText(this, "Media deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to delete media", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareMedia() {
        val media = mediaList[currentPosition]
        val file = File(media.pathUri)
        if (!file.exists()) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = if (media.type == "video") "video/*" else "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}