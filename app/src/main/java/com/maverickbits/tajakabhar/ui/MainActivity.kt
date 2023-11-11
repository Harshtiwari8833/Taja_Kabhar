package com.maverickbits.tajakabhar.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maverickbits.tajakabhar.R
import com.maverickbits.tajakabhar.db.ArticleDatabase
import com.maverickbits.tajakabhar.repositery.NewsRepositery
import com.maverickbits.tajakabhar.viewModel.NewsViewModel
import com.maverickbits.tajakabhar.viewModel.NewsViewModelFactory

class MainActivity : AppCompatActivity() {
    lateinit var viewModel : NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //creating instance of viewModel
        val newsRepositery = NewsRepositery(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelFactory(application,newsRepositery)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)

        //setting up bottom navigation
        val bottomNavigationView =   findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
    }
}