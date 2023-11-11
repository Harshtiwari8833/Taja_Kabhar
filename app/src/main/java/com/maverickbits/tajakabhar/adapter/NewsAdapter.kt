package com.maverickbits.tajakabhar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maverickbits.tajakabhar.R
import com.maverickbits.tajakabhar.databinding.ItemArticlePreviewBinding
import com.maverickbits.tajakabhar.models.Article
import java.util.Objects

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val binding = ItemArticlePreviewBinding.bind(itemView)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
          return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
           return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
         return ArticleViewHolder(
             LayoutInflater.from(parent.context).inflate(
                 R.layout.item_article_preview, parent, false
             )
         )
    }

    override fun getItemCount(): Int {
      return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
       val article = differ.currentList[position]

        holder.itemView.apply {
          Glide.with(this).load(article.urlToImage).into(holder.binding.ivArticleImage)
            holder.binding.tvSource.text = article.source.name
            holder.binding.tvTitle.text = article.title
            holder.binding.tvDescription.text = article.description
            holder.binding.tvPublishedAt.text = article.publishedAt
            setOnClickListener{
                onItemClickListener?.let {
                    it(article)
                }
            }

        }



    }

    private var onItemClickListener : ((Article)-> Unit)? = null

    fun setOnItemClickListener(listner:  (Article)-> Unit){
        onItemClickListener  = listner
    }
}