package com.example.progr3ss.utils

import android.util.Base64
import android.widget.ImageView
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.example.progr3ss.R
import com.example.progr3ss.network.RetrofitClient
import java.nio.ByteBuffer

// Kerek profilkép betöltése. A szerver a képet base64 szövegként adja vissza;
// ha az nincs, az URL-t próbáljuk, ha az sincs, a profil ikon látszik.
fun ImageView.loadAvatar(imageBase64: String?, imageUrl: String? = null) {
    if (!imageBase64.isNullOrBlank()) {
        val bytes = try {
            Base64.decode(imageBase64, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            null
        }

        if (bytes != null) {
            load(ByteBuffer.wrap(bytes)) {
                crossfade(true)
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
                transformations(CircleCropTransformation())
            }
            return
        }
    }

    val resolved = resolveImageUrl(imageUrl)
    if (resolved == null) {
        setImageResource(R.drawable.ic_profile)
        return
    }

    load(resolved) {
        crossfade(true)
        placeholder(R.drawable.ic_profile)
        error(R.drawable.ic_profile)
        transformations(CircleCropTransformation())
        memoryCachePolicy(CachePolicy.DISABLED)
        diskCachePolicy(CachePolicy.DISABLED)
    }
}

private fun resolveImageUrl(url: String?): String? {
    if (url.isNullOrBlank()) return null
    if (url.startsWith("http")) return url
    return RetrofitClient.BASE_URL.trimEnd('/') + "/" + url.trimStart('/')
}