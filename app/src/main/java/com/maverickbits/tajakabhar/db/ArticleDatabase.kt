package com.maverickbits.tajakabhar.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maverickbits.tajakabhar.models.Article

@Database(
    entities = [Article::class], version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao() : ArticleDao

    companion object{
        @Volatile
        private var instance : ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke(contex: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(contex).also { instance = it}
        }
       private fun createDatabase(contex: Context) =
           Room.databaseBuilder(
               contex.applicationContext,
               ArticleDatabase ::class.java,
               "article_db.db"
           ).build()
    }
}