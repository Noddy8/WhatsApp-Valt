package com.noddy.statussaver.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.noddy.statussaver.databinding.FragmentMediaBinding
import com.noddy.statussaver.views.adapters.MediaAdapter

class FragmentFavorites : Fragment() {
    private lateinit var binding: FragmentMediaBinding
    private lateinit var adapter: MediaAdapter
    private lateinit var favoritesRepo: FavoritesRepo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        favoritesRepo = FavoritesRepo(requireContext())
        setupRecyclerView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun setupRecyclerView() {
        adapter = MediaAdapter(ArrayList(), requireContext(), showFavoriteOption = false)
        binding.mediaRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.mediaRecyclerView.adapter = adapter
    }

    private fun loadFavorites() {
        val favorites = favoritesRepo.getFavorites()
        adapter.updateList(favorites)
        binding.tempMediaText.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
    }
}