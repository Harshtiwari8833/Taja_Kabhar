package com.maverickbits.tajakabhar.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.maverickbits.tajakabhar.repositery.NewsRepositery

class NewsViewModelFactory(val app: Application, private val newsRepositery: NewsRepositery): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>):T{
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(app,newsRepositery) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}