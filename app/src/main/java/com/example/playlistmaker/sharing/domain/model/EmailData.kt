package com.example.playlistmaker.sharing.domain.model

data class EmailData(
    val mailRecipient: String,
    val themeMessage: String,
    val message: String
)