package com.mit.learning_english.shared

object Constant {
        const val BASE_URL: String = "https://english.kimchimar3.store/api/"
        const val PAGE_SIZE_PAGE: Int = 5
        const val MAX_SIZE_PAGE: Int = 50
        const val JUMP_THRESHOLD: Int = 20
//    const val BASE_URL: String = "http://10.0.2.2:7000/api/"

        const val DEEP_LINK_SCHEME = "flulingo"
        const val DEEP_LINK_HOST_BOOK = "book"
        const val DEEP_LINK_BOOK_URI = "$DEEP_LINK_SCHEME://$DEEP_LINK_HOST_BOOK"

        const val DEEP_LINK_HTTPS_HOST = "english.kimchimar3.store"

        const val DEEP_LINK_HTTPS_PATH_PREFIX = "/book/"
        const val DEEP_LINK_HTTPS_BOOK_URL = "https://$DEEP_LINK_HTTPS_HOST$DEEP_LINK_HTTPS_PATH_PREFIX"
}
