package com.maverickbits.tajakabhar.models

import com.maverickbits.tajakabhar.models.Article

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)