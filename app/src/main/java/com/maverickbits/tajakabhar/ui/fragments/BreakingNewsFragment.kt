package com.maverickbits.tajakabhar.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maverickbits.tajakabhar.R
import com.maverickbits.tajakabhar.adapter.NewsAdapter
import com.maverickbits.tajakabhar.databinding.FragmentBreakingNewsBinding
import com.maverickbits.tajakabhar.models.Article
import com.maverickbits.tajakabhar.models.NewsResponse
import com.maverickbits.tajakabhar.ui.MainActivity
import com.maverickbits.tajakabhar.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.maverickbits.tajakabhar.utils.Resource
import com.maverickbits.tajakabhar.viewModel.NewsViewModel

class BreakingNewsFragment : Fragment(){

    lateinit var viewModel : NewsViewModel
    lateinit var newAdapter : NewsAdapter
    val TAG = "BreakingNewsFragment"
    private lateinit var binding : FragmentBreakingNewsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBreakingNewsBinding.inflate(inflater,container,false)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()



//        newAdapter.setOnItemClickListener {
//            Toast.makeText(requireContext(), "hello", Toast.LENGTH_LONG).show()
//            val bundle  =  Bundle().apply {
//                putSerializable("article", it)
//            }
//
//            findNavController().navigate(
//                R.id.action_breakingNewsFragment_to_articleFragment,bundle
//            )
//        }

        newAdapter.setOnItemClickListener { item ->
            Toast.makeText(requireContext(), "Hello", Toast.LENGTH_LONG).show()

            val bundle = Bundle().apply {
                putSerializable("article", item)
            }

            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment, bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer {
            when(it){
                 is Resource.Success ->{
                     hideProgressBar()
                     it.data?.let {newsResponse->
                         newAdapter.differ.submitList(newsResponse.articles.toList()
                         )
                         val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE +2
                         isLastPage = viewModel.breakingNewsPage== totalPages

                         if(isLastPage){
                             binding.rvBreakingNews.setPadding(0,0,0,0)
                         }

                     }
                 }
                is Resource.Error->{
                    hideProgressBar()
                    it.message?.let {message->
                       Toast.makeText(activity, "An error occured, ${message}" , Toast.LENGTH_LONG).show()
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
        isLoading =false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading =true
    }
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
              val layoutManager = recyclerView.layoutManager as LinearLayoutManager
              val firstVisibleItemPosition= layoutManager.findFirstVisibleItemPosition()
             val visibleItemCount  = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem =  firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBegning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate  =  isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBegning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate){
                viewModel.getBreakingNews("in")
                isScrolling =false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }else{

            }
        }


    }

    private fun setupRecyclerView(){
        newAdapter =  NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter= newAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }



}