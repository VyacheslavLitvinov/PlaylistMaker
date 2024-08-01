package com.example.playlistmaker.domain.api

sealed class ConsumerData<T> {
    data class Data<T>(val data: T) : ConsumerData<T>()
    data class Error<T>(val message: String) : ConsumerData<T>()
}