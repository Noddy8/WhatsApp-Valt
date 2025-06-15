package com.noddy.statussaver.views.fragments

import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.noddy.statussaver.databinding.FragmentCalendarBinding
import com.noddy.statussaver.models.MediaModel
import com.noddy.statussaver.views.adapters.MediaAdapter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FragmentCalendar : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private val statusMap = mutableMapOf<Long, MutableList<File>>()
    private lateinit var adapter: MediaAdapter
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        loadStatusesByDate()
        setupCalendar()
        setupRecyclerView()
        return binding.root
    }

    private fun loadStatusesByDate() {
        val folder = FileUtils.getStatusFolder(requireContext())

        if (folder.exists() && folder.isDirectory) {
            folder.listFiles()?.forEach { file ->
                if (file.isFile) {
                    val cal = Calendar.getInstance().apply {
                        timeInMillis = file.lastModified()
                    }
                    val dayStart = cal.apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis

                    statusMap.getOrPut(dayStart) { mutableListOf() }.add(file)
                }
            }
        }
    }

    private fun setupCalendar() {
        binding.calendarView.setOnDateChangedListener { year, month, day ->
            val cal = Calendar.getInstance().apply {
                set(year, month, day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            binding.selectedDate.text = "Selected: ${dateFormat.format(cal.time)}"
            showStatusesForDate(cal.timeInMillis)
        }

        // Set initial date to today
        val today = Calendar.getInstance()
        binding.selectedDate.text = "Selected: ${dateFormat.format(today.time)}"
        showStatusesForDate(today.timeInMillis)
    }

    private fun setupRecyclerView() {
        adapter = MediaAdapter(ArrayList(), requireContext())
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.adapter = adapter
    }

    private fun showStatusesForDate(dayStart: Long) {
        val files = statusMap[dayStart] ?: emptyList()
        val mediaList = files.map { file ->
            MediaModel(
                pathUri = Uri.fromFile(file).toString(),
                fileName = file.name,
                type = if (file.extension == "mp4") "video" else "image"
            )
        }
        adapter.updateList(ArrayList(mediaList))
    }
}