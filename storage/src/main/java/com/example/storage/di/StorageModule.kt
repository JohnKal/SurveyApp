package com.example.storage.di

import com.example.storage.session.SessionManager
import com.example.storage.session.SessionManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    @Provides
    internal fun provideSessionManager(): SessionManagerImpl {
        return SessionManager
    }
}