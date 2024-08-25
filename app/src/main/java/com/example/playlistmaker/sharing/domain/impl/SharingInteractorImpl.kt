package com.example.playlistmaker.sharing.domain.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context,
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
        return context.getString(R.string.share_link)
    }

    private fun getSupportEmailData() : EmailData {
        return EmailData(
            mailRecipient = context.getString(R.string.mail_recipient),
            themeMessage = context.getString(R.string.theme_message),
            message = context.getString(R.string.message)
        )
    }

    private fun getTermsLink() : String {
        return context.getString(R.string.url_terms)
    }


}