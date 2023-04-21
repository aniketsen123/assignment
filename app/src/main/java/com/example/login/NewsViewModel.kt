package com.example.login

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.androiddevs.mvvmnewsapp.util.Resource
import com.example.login.models.NewsResponse
import com.example.login.repository.NewsRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app:Application,
    val newsRepository: NewsRepository
) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null
    val categorynews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var categorynewsPage = 1
    var handleNewsResponse: NewsResponse? = null
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchingNewsResponse: NewsResponse? = null
    var newSearchQuery:String? = null
    var oldSearchQuery:String? = null



    init {
        getBreakingNews("in")
    }
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
       // breakingNews.postValue(Resource.Loading())
        safeBreakingNews(countryCode)
    }

    fun getSearchNews(searchQuery:String)=viewModelScope.launch {
        safeSearchingNews(searchQuery)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {

            response.body()?.let { resultResponse ->
                breakingNewsPage++;
                if(breakingNewsResponse==null)
                {
                    breakingNewsResponse=resultResponse
                }
                else
                {
                    val oldArticles=breakingNewsResponse?.articles
                    val newArticle=resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(breakingNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNews(response: Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++;
                if(searchingNewsResponse==null)
                {
                    searchingNewsResponse=resultResponse
                }
                else
                {
                    val oldArticles=searchingNewsResponse?.articles
                    val newArticle=resultResponse.articles
                    oldArticles?.addAll(newArticle)
                }
                return Resource.Success(searchingNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    private suspend fun safeBreakingNews(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection())
            {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }
           else
            {
                breakingNews.postValue(Resource.Error("NO Internet COnnection"))
            }
        }catch (t:Throwable)
        {
         when(t){
             is IOException->breakingNews.postValue((Resource.Error("Network Failed")))
             else ->breakingNews.postValue((Resource.Error("Conversion Error")))
         }
        }
    }
    private suspend fun safeSearchingNews(Query: String){
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection())
            {
                val response = newsRepository.searchnews(Query, searchNewsPage)
                searchNews.postValue(handleSearchNews(response))
            }
            else
            {
                searchNews.postValue(Resource.Error("NO Internet COnnection"))
            }
        }catch (t:Throwable)
        {
            when(t){
                is IOException->searchNews.postValue((Resource.Error("Network Failed")))
                else ->searchNews.postValue((Resource.Error("Conversion Error")))
            }
        }
    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
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


