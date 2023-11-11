package com.maverickbits.tajakabhar.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.maverickbits.tajakabhar.R
import com.maverickbits.tajakabhar.databinding.FragmentArticleBinding
import com.maverickbits.tajakabhar.databinding.FragmentBreakingNewsBinding
import com.maverickbits.tajakabhar.ui.MainActivity
import com.maverickbits.tajakabhar.viewModel.NewsViewModel


class ArticleFragment : Fragment() {
    lateinit var binding: FragmentArticleBinding
    lateinit var viewModel : NewsViewModel
    val args : ArticleFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleBinding.inflate(inflater,container,false)

        viewModel = (activity as MainActivity).viewModel

        val article = args.article

        binding.webView.apply {
            webViewClient  = WebViewClient()
            loadUrl(article.url)
        }


        //handling favourite

        binding.fab.setOnClickListener{
            viewModel.saveArticle(article)
            view?.let { it1 -> Snackbar.make(it1, "Article saved successfully", Snackbar.LENGTH_SHORT).show() }
        }
        return binding.root
    }

}