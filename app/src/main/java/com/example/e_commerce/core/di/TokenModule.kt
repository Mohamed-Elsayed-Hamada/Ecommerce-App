package com.example.e_commerce.core.di

import com.example.e_commerce.core.TokenManager
import com.example.e_commerce.core.TokenStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenModule {

    @Binds
    @Singleton
    abstract fun bindTokenStore(impl: TokenManager): TokenStore
}
