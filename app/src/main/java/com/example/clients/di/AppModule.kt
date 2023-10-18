package com.example.clients.di

import android.content.Context
import com.example.clients.data.ClientsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideClientRepository(@ApplicationContext context: Context): ClientsRepository =
        ClientsRepository(context)
}