package com.noddy.statussaver.views.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.noddy.statussaver.R
import com.noddy.statussaver.data.StatusRepo
import com.noddy.statussaver.databinding.ActivityBusinessStatusBinding
import com.noddy.statussaver.utils.Constants
import com.noddy.statussaver.utils.SharedPrefKeys
import com.noddy.statussaver.utils.SharedPrefUtils
import com.noddy.statussaver.utils.getFolderPermissions
import com.noddy.statussaver.viewmodels.factories.StatusViewModel
import com.noddy.statussaver.viewmodels.factories.StatusViewModelFactory
import com.noddy.statussaver.views.adapters.MediaViewPagerAdapter
import kotlinx.coroutines.launch

class BusinessStatusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBusinessStatusBinding
    private val viewModel: StatusViewModel by viewModels {
        StatusViewModelFactory(StatusRepo(this))
    }
    private val WHATSAPP_BUSINESS_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBar.setNavigationOnClickListener { finish() }

        SharedPrefUtils.init(this)
        setupViewPager()
        checkPermissionsAndLoad()
    }
    
    private fun setupViewPager() {
        val adapter = MediaViewPagerAdapter(
            this,
            imagesType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES,
            videosType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS
        )
        binding.viewPager.adapter = adapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Images"
                1 -> "Videos"
                else -> null
            }
        }.attach()
    }

    private fun checkPermissionsAndLoad() {
        if (viewModel.isWhatsAppBusinessPermissionGranted()) {
            loadBusinessStatuses()
        } else {
            requestPermissions()
        }
    }

    private fun loadBusinessStatuses() {
        lifecycleScope.launch {
            viewModel.getWhatsAppBusinessStatuses()
        }
    }

    private fun requestPermissions() {
        getFolderPermissions(
            context = this,
            REQUEST_CODE = WHATSAPP_BUSINESS_REQUEST_CODE,
            initialUri = Constants.getWhatsappBusinessUri()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val treeUri = data?.data!!
            contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            if (requestCode == WHATSAPP_BUSINESS_REQUEST_CODE) {
                SharedPrefUtils.putPrefString(
                    SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI,
                    treeUri.toString()
                )
                SharedPrefUtils.putPrefBoolean(
                    SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
                    true
                )
                loadBusinessStatuses()
            }
        }
    }
}