package com.example.progr3ss.utils

import android.widget.ImageView
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.example.progr3ss.R
import com.example.progr3ss.network.RetrofitClient

// Kerek profilkép betöltése; ha nincs kép, a profil ikon látszik
fun ImageView.loadAvatar(url: String?) {
    val resolved = resolveImageUrl(url)
    if (resolved == null) {
        setImageResource(R.drawable.ic_profile)
        return
    }
    load(resolved) {
        crossfade(true)
        placeholder(R.drawable.ic_profile)
        error(R.drawable.ic_profile)
        transformations(CircleCropTransformation())
        // Feltöltés után azonnal az új kép látszódjon
        memoryCachePolicy(CachePolicy.DISABLED)
        diskCachePolicy(CachePolicy.DISABLED)
    }
}

private fun resolveImageUrl(url: String?): String? {
    if (url.isNullOrBlank()) return null
    if (url.startsWith("http")) return url
    return RetrofitClient.BASE_URL.trimEnd('/') + "/" + url.trimStart('/')
}