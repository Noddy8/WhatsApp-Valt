package com.noddy.statussaver.views.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.a914_gowtham.libvideoeditor.VideoTrimmer
import com.github.a914_gowtham.libvideoeditor.interfaces.VideoTrimListener
import com.noddy.statussaver.databinding.ActivityVideoTrimmerBinding

class VideoTrimmerActivity : AppCompatActivity(), VideoTrimListener { // Changed interface
    private lateinit var binding: ActivityVideoTrimmerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoTrimmerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoUri = intent.getStringExtra("VIDEO_URI")?.let { Uri.parse(it) }
        videoUri?.let { setupVideoTrimmer(it) }

        binding.btnSave.setOnClickListener {
            binding.videoTrimmer.saveTrimmedVideo() // Changed method name
        }
    }

    private fun setupVideoTrimmer(videoUri: Uri) {
        binding.videoTrimmer.apply {
            setVideoTrimListener(this@VideoTrimmerActivity) // Changed listener setup
            setVideoURI(videoUri) // Same but different internal handling

            // Set output path (required)
            setDestination(
                getExternalFilesDir(null)?.absolutePath
                    ?: "${filesDir.absolutePath}/trimmed"
            )

            setMaxDurationInSeconds(30) // Changed method name
            // Optional: Customize appearance
            setAccentColor(ContextCompat.getColor(this@VideoTrimmerActivity, R.color.your_accent_color))
        }
    }

    // Updated callback methods
    override fun onTrimStarted() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onTrimCompleted(trimmedVideoPath: String) { // Now returns String path
        binding.progressBar.visibility = View.GONE
        val resultIntent = Intent().apply {
            putExtra("TRIMMED_PATH", trimmedVideoPath) // Direct path string
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onCancel() { // Renamed method
        finish()
    }

    override fun onError(throwable: Throwable) { // Now receives Throwable
        binding.progressBar.visibility = View.GONE
        Toast.makeText(
            this,
            "Error: ${throwable.localizedMessage}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoTrimmer.release() // Important: Release resources
    }
}