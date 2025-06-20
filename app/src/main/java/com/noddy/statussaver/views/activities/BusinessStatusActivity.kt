package com.noddy.statussaver.views.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
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

        // Check if WhatsApp Business is installed
        if (isPackageInstalled(Constants.TYPE_WHATSAPP_BUSINESS)) {
            SharedPrefUtils.init(this)
            setupViewPager()
            checkPermissionsAndLoad()
        } else {
            Toast.makeText(this, "WhatsApp Business is not installed", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
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

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshBusinessStatuses()
        }
    }

    private fun refreshBusinessStatuses() {
        viewModel.refreshWhatsAppBusinessStatuses()
        Toast.makeText(this, "Refreshing WP Statuses", Toast.LENGTH_SHORT)
            .show()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.swipeRefreshLayout.isRefreshing = false
        }, 2000)
    }

    private fun checkPermissionsAndLoad() {
        if (viewModel.isWhatsAppBusinessPermissionGranted()) {
            loadBusinessStatuses()
        } else {
            requestPermissions()
        }
        setupSwipeRefresh()
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