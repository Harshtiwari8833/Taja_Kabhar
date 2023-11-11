package com.maverickbits.tajakabhar.ui.fragments

import android.media.browse.MediaBrowser.ItemCallback
import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.room.util.newStringBuilder
import com.google.android.material.snackbar.Snackbar
import com.maverickbits.tajakabhar.R
import com.maverickbits.tajakabhar.adapter.NewsAdapter
import com.maverickbits.tajakabhar.databinding.FragmentArticleBinding
import com.maverickbits.tajakabhar.databinding.FragmentSavedNewsBinding
import com.maverickbits.tajakabhar.models.Article
import com.maverickbits.tajakabhar.ui.MainActivity
import com.maverickbits.tajakabhar.viewModel.NewsViewModel

class SavedNewsFragment : Fragment() {

  lateinit var binding: FragmentSavedNewsBinding
    lateinit var viewModel : NewsViewModel
    lateinit var newAdapter : NewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentSavedNewsBinding.inflate(inflater,container,false)
        setupRecyclerView()
        newAdapter.setOnItemClickListener {
            val bundle  =  Bundle().apply {
                putSerializable("article", it)
            }

            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,bundle
            )
        }
val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
){
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
       return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
       val position = viewHolder.adapterPosition
        val article = newAdapter.differ.currentList[position]
        viewModel.deleteArticle(article)

        view?.let {
            Snackbar.make(it, "Successfully deleted article", Snackbar.LENGTH_SHORT).apply {
                setAction("Undo"){
                    viewModel.saveArticle(article)
                }
                show()
            }
        }

    }



}

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }
        viewModel = (activity as MainActivity).viewModel


        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer {articles->
            newAdapter.differ.submitList(articles)

        })

        return binding.root
    }

    private fun setupRecyclerView(){
        newAdapter =  NewsAdapter()
        binding.rvSavedNews.apply {
            adapter= newAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }



}