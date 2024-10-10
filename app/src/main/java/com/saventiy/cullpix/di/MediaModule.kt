package com.saventiy.cullpix.di

import com.saventiy.cullpix.repository.MediaRepository
import com.saventiy.cullpix.repository.MediaRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Singleton
    @Provides
    fun provideMediaRepositoryImpl(): MediaRepository {
        return MediaRepositoryImpl()
    }
}