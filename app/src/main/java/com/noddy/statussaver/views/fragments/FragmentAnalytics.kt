package com.noddy.statussaver.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.FileUtils
import com.noddy.statussaver.databinding.FragmentAnalyticsBinding
import kotlin.math.log10
import kotlin.math.pow

class FragmentAnalytics : Fragment() {
    private lateinit var binding: FragmentAnalyticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        loadAnalytics()
        return binding.root
    }

    private fun loadAnalytics() {
        val folder = FileUtils.getStatusFolder(requireContext())

        if (folder.exists() && folder.isDirectory) {
            val files = folder.listFiles()
            val imageCount = files?.count { it.extension in listOf("jpg", "jpeg", "png") } ?: 0
            val videoCount = files?.count { it.extension == "mp4" } ?: 0

            setupPieChart(binding.pieChart, imageCount, videoCount)

            val totalSize = files?.sumOf { it.length() } ?: 0
            binding.txtTotalSize.text = "Total Size: ${formatFileSize(totalSize)}"
            binding.txtTotalFiles.text = "Total Files: ${files?.size ?: 0}"
        }
    }

    private fun setupPieChart(chart: PieChart, images: Int, videos: Int) {
        val entries = listOf(
            PieEntry(images.toFloat(), "Images"),
            PieEntry(videos.toFloat(), "Videos")
        )

        val dataSet = PieDataSet(entries, "Status Types").apply {
            colors = ColorTemplate.MATERIAL_COLORS.asList()
            valueTextSize = 12f
        }

        chart.data = PieData(dataSet)
        chart.description.isEnabled = false
        chart.legend.isEnabled = true
        chart.setEntryLabelTextSize(12f)
        chart.animateY(1000)
        chart.invalidate()
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return "%.1f %s".format(size / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
    }
}