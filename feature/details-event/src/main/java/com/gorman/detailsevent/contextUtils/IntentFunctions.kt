package com.gorman.detailsevent.contextUtils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri

fun openBrowser(context: Context, url: String?) {
    if (url.isNullOrBlank()) return
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show()
    }
}

fun shareContent(context: Context, content: String?) {
    if (content.isNullOrBlank()) return
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(content, Intent.EXTRA_TEXT)
        type = "text/plain"
    }
    val sendIntent = Intent.createChooser(shareIntent, null)
    context.startActivity(sendIntent)
}

fun openMap(context: Context, coordinates: String?) {
    if (coordinates.isNullOrBlank()) return
    try {
        val uri = "geo:$coordinates?q=$coordinates".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Приложение карт не найдено", Toast.LENGTH_SHORT).show()
    }
}
