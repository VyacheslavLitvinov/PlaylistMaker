package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    private companion object {
        const val TYPE_SHARE_BUTTON = "text/plain"
        const val DATA_HELP_BUTTON = "mailto:"
        const val SHARE_TITTLE = "Share"
    }
    override fun shareLink(link: String) {
        val intentShareLink = Intent(Intent.ACTION_SEND)
        intentShareLink.type = TYPE_SHARE_BUTTON
        intentShareLink.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_link))
        intentShareLink.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(Intent.createChooser(intentShareLink, SHARE_TITTLE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openLink(link: String) {
        val url = Uri.parse(context.getString(R.string.url_terms))
        val intentOpenLink = Intent(Intent.ACTION_VIEW, url)
        intentOpenLink.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intentOpenLink)
    }

    override fun openEmail(link: String) {
        val intentOpenEmail = Intent(Intent.ACTION_SENDTO)
        intentOpenEmail.data = Uri.parse(DATA_HELP_BUTTON)
        intentOpenEmail.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(context.getString(R.string.mail_recipient))
        )
        intentOpenEmail.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.theme_message))
        intentOpenEmail.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.message))
        intentOpenEmail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (intentOpenEmail.resolveActivity(context.packageManager) != null) {
            context.startActivity(intentOpenEmail)
        } else {
            Toast.makeText(context, "Нет доступных приложений для отправки сообщения", Toast.LENGTH_SHORT).show()
        }
    }

}