package com.jarlingwar.adminapp.di

import android.content.Context
import android.location.Geocoder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jarlingwar.adminapp.data.firebase.ChatRemoteRepositoryImpl
import com.jarlingwar.adminapp.data.firebase.ListingsRemoteRepositoryImpl
import com.jarlingwar.adminapp.data.firebase.ReviewRepositoryImpl
import com.jarlingwar.adminapp.data.firebase.UsersRemoteRepositoryImpl
import com.jarlingwar.adminapp.domain.repositories.remote.IChatRepository
import com.jarlingwar.adminapp.domain.repositories.remote.IListingsRemoteRepository
import com.jarlingwar.adminapp.domain.repositories.remote.IReviewRepository
import com.jarlingwar.adminapp.domain.repositories.remote.IUsersRemoteRepository
import com.jarlingwar.adminapp.utils.RemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideFirestore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context) = Geocoder(context, Locale.ENGLISH)

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideUsersRemoteRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): IUsersRemoteRepository {
        return UsersRemoteRepositoryImpl(firebaseAuth, firestore)
    }

    @Provides
    @Singleton
    fun provideChatRepository(firestore: FirebaseFirestore): IChatRepository {
        return ChatRemoteRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideReviewRepository(firestore: FirebaseFirestore): IReviewRepository {
        return ReviewRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideListingsRemoteRepository(firestore: FirebaseFirestore, remoteConfig: RemoteConfig): IListingsRemoteRepository {
        return ListingsRemoteRepositoryImpl(remoteConfig, firestore)
    }

    @Provides
    @Singleton
    fun provideRemoteConfig() = RemoteConfig.getRemoteConfig()
}