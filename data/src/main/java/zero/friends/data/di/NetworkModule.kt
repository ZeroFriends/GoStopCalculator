package zero.friends.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import zero.friends.data.source.api.RuleApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Provides
    @Singleton
    fun provideOkhttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BASIC)
                }
            )
            .build()

    @Provides
    @Singleton
    fun provideConverter(): Converter.Factory {
        return Json {
            ignoreUnknownKeys = true
        }.asConverterFactory("application/json".toMediaType())
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        convertFactory: Converter.Factory,
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/")
        .addConverterFactory(convertFactory)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideRuleApi(
        retrofit: Retrofit,
    ): RuleApi = retrofit.create(RuleApi::class.java)
}