package com.mit.learning_english.shared

object Constant {
    /**
     * IPv4 của adapter **Wi‑Fi** (cùng mạng với điện thoại). Không dùng IP Radmin/Tailscale/VMware
     * trừ khi điện thoại cũng kết nối qua đúng dịch vụ đó.
     * Emulator: `http://10.0.2.2:7000/api/`
     */
    const val BASE_URL: String = "http://192.168.22.113:7000/api/"

    const val PAGE_SIZE_PAGE: Int = 5
    const val MAX_SIZE_PAGE: Int = 50
    const val JUMP_THRESHOLD: Int = 20
}
