package com.maverickbits.tajakabhar.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maverickbits.NewsApplication
import com.maverickbits.tajakabhar.models.Article
import com.maverickbits.tajakabhar.models.NewsResponse
import com.maverickbits.tajakabhar.repositery.NewsRepositery
import com.maverickbits.tajakabhar.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(app: Application
    ,val newsRepositery : NewsRepositery) : AndroidViewModel(app) {
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage =1
    var breakingNewsResponse : NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage =1
    var searchNewsResponse : NewsResponse? = null
    init {
        getBreakingNews("in")
    }
    fun getBreakingNews(countryCode: String){
        viewModelScope.launch(Dispatchers.IO) {
         safeBreakingNewsCall(countryCode)

        }
    }

    fun searchNews(searchQuery:String){
        viewModelScope.launch(Dispatchers.IO) {
           safeSearchNewsCall(searchQuery)
        }

    }


    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if (response.isSuccessful){
            Log.d("NewsViewModel", response.toString())
            response.body()?.let {
                breakingNewsPage++
                if (breakingNewsResponse == null){
                    breakingNewsResponse = it
                }else{
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticle = it.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(breakingNewsResponse ?: it)
            }
        }
        else{
            Log.d("NewsViewModel", "no")
        }
       return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if (response.isSuccessful){
            Log.d("NewsViewModel", response.toString())
            response.body()?.let {
                breakingNewsPage++
                if (searchNewsResponse == null){
                    searchNewsResponse = it
                }else{
                    val oldArticles = searchNewsResponse?.articles
                    val newArticle = it.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(searchNewsResponse ?: it)
            }
        }
        else{
            Log.d("NewsViewModel", "no")
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article)  = viewModelScope.launch {
        newsRepositery.upsert(article)
    }

    fun getSavedNews() = newsRepositery.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepositery.deleteArticle(article)
    }

    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = newsRepositery.searchNews(searchQuery,searchNewsPage)
                Log.d("NewsViewModel", response.toString())
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }

        }catch (t:Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else-> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private suspend fun safeBreakingNewsCall(countryCode: String){
   breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = newsRepositery.getBreakingNews(countryCode,breakingNewsPage)
                Log.d("NewsViewModel", response.toString())
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }

        }catch (t:Throwable){
           when(t){
               is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
               else-> breakingNews.postValue(Resource.Error("Conversion Error"))
           }
        }
    }

    private fun hasInternetConnection(): Boolean{
        val connectiveManager = getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectiveManager.activeNetwork ?: return false
            val capabilities = connectiveManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) ->  true
                capabilities.hasTransport(TRANSPORT_CELLULAR) ->  true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false

            }

        }else{
            connectiveManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}