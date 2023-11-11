package com.maverickbits.tajakabhar.ui.fragments

import android.os.Binder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.maverickbits.tajakabhar.R
import com.maverickbits.tajakabhar.adapter.NewsAdapter
import com.maverickbits.tajakabhar.databinding.FragmentSavedNewsBinding
import com.maverickbits.tajakabhar.databinding.FragmentSearchNewsFragmentsBinding
import com.maverickbits.tajakabhar.models.Article
import com.maverickbits.tajakabhar.ui.MainActivity
import com.maverickbits.tajakabhar.utils.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.maverickbits.tajakabhar.utils.Resource
import com.maverickbits.tajakabhar.viewModel.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragments : Fragment() {

    lateinit var binding: FragmentSearchNewsFragmentsBinding
    lateinit var viewModel : NewsViewModel
    lateinit var newAdapter : NewsAdapter
    val TAG = "SearchNewsFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentSearchNewsFragmentsBinding.inflate(inflater,container,false)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()

        newAdapter.setOnItemClickListener {
            val bundle  =  Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_searchNewsFragments_to_articleFragment,bundle
            )
        }


        //implementing search functionality

        var job : Job? = null
        binding.etSearch.addTextChangedListener {editable->
            job?.cancel()
            job=  MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                   if (editable.toString().isNotEmpty()){
                       viewModel.searchNews(editable.toString())
                   }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success ->{
                    hideProgressBar()
                    it.data?.let {newsResponse->
                        newAdapter.differ.submitList(newsResponse.articles)

                    }
                }
                is Resource.Error->{
                    hideProgressBar()
                    it.message?.let {message->
                        Log.e(TAG,message)
                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }
                else -> {

                }
            }
        })
        return binding.root
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
    }
    private fun setupRecyclerView(){
        newAdapter =  NewsAdapter()
        binding.rvSearchNews.apply {
            adapter= newAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }



}