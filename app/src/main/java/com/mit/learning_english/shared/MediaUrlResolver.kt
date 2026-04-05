package com.mit.learning_english.shared

import android.net.Uri

/**
 * Chỉnh host ảnh khi server trả về URL kiểu emulator (`10.0.2.2` / localhost) nhưng app đang
 * gọi API bằng IP LAN trong [Constant.BASE_URL]. Không đổi URL public/https hợp lệ.
 */
object MediaUrlResolver {

    fun resolve(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val base = try {
            Uri.parse(Constant.BASE_URL.trimEnd('/'))
        } catch (_: Exception) {
            return raw
        }
        val targetAuthority = base.authority ?: return raw
        return try {
            val u = Uri.parse(raw)
            if (!u.isHierarchical || u.host.isNullOrEmpty()) return raw
            val h = u.host!!.lowercase()
            val needsRewrite = h == "10.0.2.2" || h == "127.0.0.1" || h == "localhost"
            if (!needsRewrite) return raw
            u.buildUpon()
                .scheme(base.scheme ?: "http")
                .encodedAuthority(targetAuthority)
                .build()
                .toString()
        } catch (_: Exception) {
            raw
        }
    }
}
