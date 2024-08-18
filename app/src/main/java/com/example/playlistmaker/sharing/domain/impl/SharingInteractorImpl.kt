package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        val emailData = getSupportEmailData().mailRecipient
        externalNavigator.openEmail(emailData)
    }

    private fun getShareAppLink(): String {
        return "https://practicum.yandex.ru/android-developer/"
    }

    private fun getSupportEmailData() : EmailData {
        return EmailData(
            mailRecipient = "wyacheslavlitvinov@yandex.ru",
            themeMessage = "Сообщение разработчикам и разработчицам приложения Playlist Maker",
            message = "Спасибо разработчикам и разработчицам за крутое приложение!"
        )
    }

    private fun getTermsLink() : String {
        return "https://yandex.ru/legal/practicum_offer/"
    }


}