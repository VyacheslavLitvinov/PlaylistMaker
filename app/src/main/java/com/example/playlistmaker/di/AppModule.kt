package com.example.playlistmaker.di

import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}