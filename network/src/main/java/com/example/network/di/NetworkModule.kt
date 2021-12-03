package com.example.network.di

import com.example.network.api.ServiceEndpoints
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    internal fun provideService(retrofit: Retrofit): ServiceEndpoints {
        return retrofit.create(ServiceEndpoints::class.java)
    }
}