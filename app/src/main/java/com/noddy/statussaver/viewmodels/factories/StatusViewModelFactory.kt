package com.noddy.statussaver.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noddy.statussaver.data.StatusRepo
import com.noddy.statussaver.utils.SharedPrefKeys
import com.noddy.statussaver.utils.SharedPrefUtils

class StatusViewModelFactory(private val repo: StatusRepo) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatusViewModel(repo) as T
    }
}