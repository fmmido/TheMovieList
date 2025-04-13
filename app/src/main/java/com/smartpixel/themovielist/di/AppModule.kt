package com.smartpixel.themovielist.di

import android.content.Context
import androidx.room.Room
import com.smartpixel.themovielist.data.api.MovieApi
import com.smartpixel.themovielist.data.local.MovieDatabase
import com.smartpixel.themovielist.data.local.MovieDao
import com.smartpixel.themovielist.data.room.RemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    private const val TMDB_BEARER_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI0ZTRhODU0ODVmNzc3MmVjODE2Y2UwODk0OGY3MzhiMyIsIm5iZiI6MTc0NDM4NTY4Ni43ODU5OTk4LCJzdWIiOiI2N2Y5MzY5NmQ0YzQ0NGExY2M5OWQ3NzQiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.7HTvMwhEF1P1yiW4a8-oD1y6h7dGpJbLNbwtgFeVy_4"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $TMDB_BEARER_TOKEN")
                    .addHeader("accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieApi(retrofit: Retrofit): MovieApi {
        return retrofit.create(MovieApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieDatabase(@ApplicationContext context: Context): MovieDatabase {
        return Room.databaseBuilder(context, MovieDatabase::class.java, "movie_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(database: MovieDatabase): MovieDao = database.movieDao()

    @Provides
    @Singleton
    fun provideRemoteKeyDao(database: MovieDatabase): RemoteKeyDao = database.remoteKeyDao()

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
}