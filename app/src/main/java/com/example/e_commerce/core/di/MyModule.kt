package com.example.e_commerce.core.di

import com.example.e_commerce.core.SessionManager
import com.example.e_commerce.core.TokenStore
import com.example.e_commerce.data.auth.AuthApiService
import com.example.e_commerce.data.brands.BrandApiService
import com.example.e_commerce.data.cart_wishlist.CartApiService
import com.example.e_commerce.data.cart_wishlist.WishlistApiService
import com.example.e_commerce.data.category.CategoryApiService
import com.example.e_commerce.data.products.ProductsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PublicRetrofit


@Module
@InstallIn(SingletonComponent::class)
object MyModule {

    @Provides
    @Singleton
    @PublicRetrofit
    fun providePublicRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://ecommerce.routemisr.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        tokenStore: TokenStore,
        sessionManager: SessionManager
    ): Retrofit {

        val authInterceptor = Interceptor { chain ->

            val token = tokenStore.getToken() ?: ""

            val request = chain.request()
                .newBuilder()
                .addHeader("token", token)
                .build()

            val response = chain.proceed(request)
            if (response.code == 401) {
                sessionManager.onUnauthorized()
            }
            response
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://ecommerce.routemisr.com/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideAuthApiService(
        @PublicRetrofit retrofit: Retrofit
    ): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideProductsApiService(
        @PublicRetrofit retrofit: Retrofit
    ): ProductsApiService {
        return retrofit.create(ProductsApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideCategoryApiService(
        @PublicRetrofit retrofit: Retrofit
    ): CategoryApiService {
        return retrofit.create(CategoryApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideBrandApiService(
        @PublicRetrofit retrofit: Retrofit
    ): BrandApiService {
        return retrofit.create(BrandApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideCartApiService(
        @AuthRetrofit retrofit: Retrofit
    ): CartApiService {
        return retrofit.create(CartApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWishlistApiService(
        @AuthRetrofit retrofit: Retrofit
    ): WishlistApiService {
        return retrofit.create(WishlistApiService::class.java)
    }

}
