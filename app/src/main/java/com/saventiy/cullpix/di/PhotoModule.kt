package com.saventiy.cullpix.di

import android.content.Context
import com.saventiy.cullpix.repository.impl.PhotoRepository
import com.saventiy.cullpix.repository.impl.PhotoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PhotoModule {

    @Singleton
    @Provides
    fun providePhotoRepositoryImpl(@ApplicationContext context: Context): PhotoRepository {
        return PhotoRepositoryImpl(context)
    }
}