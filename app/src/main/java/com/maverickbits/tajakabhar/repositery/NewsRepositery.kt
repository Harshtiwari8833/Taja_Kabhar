package com.maverickbits.tajakabhar.repositery

import com.maverickbits.tajakabhar.api.RetrofitInstance
import com.maverickbits.tajakabhar.db.ArticleDatabase
import com.maverickbits.tajakabhar.models.Article
import retrofit2.Retrofit

class NewsRepositery(val db : ArticleDatabase) {
suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
    RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery : String, pageNumber: Int)= RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews()= db.getArticleDao().getAllArticles()


    suspend fun deleteArticle(article: Article) = db.getArticleDao().Delete(article)
}