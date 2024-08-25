package com.example.playlistmaker.sharing.data

interface ExternalNavigator {

    fun shareLink(link: String)

    fun openLink(link: String)

    fun openEmail(link: String)

}