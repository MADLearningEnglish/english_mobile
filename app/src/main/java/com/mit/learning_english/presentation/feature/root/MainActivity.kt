package com.mit.learning_english.presentation.feature.root

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mit.learning_english.R
import com.mit.learning_english.shared.Constant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
//        handleDeepLinkIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLinkIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        val bookId = extractBookId(uri) ?: return
        pendingDeepLinkBookId = bookId
    }

    private fun extractBookId(uri: Uri): Int? {
        // flulingo://book/{bookId}
        if (uri.scheme == Constant.DEEP_LINK_SCHEME && uri.host == Constant.DEEP_LINK_HOST_BOOK) {
            return uri.pathSegments?.firstOrNull()?.toIntOrNull()
        }
        // https://flulingo.com/book/{bookId}
        if (uri.scheme == "https" && uri.host == Constant.DEEP_LINK_HTTPS_HOST) {
            val segments = uri.pathSegments ?: return null
            if (segments.size >= 2 && segments[0] == Constant.DEEP_LINK_HOST_BOOK) {
                return segments[1].toIntOrNull()
            }
        }
        return null
    }

    companion object {
        var pendingDeepLinkBookId: Int? = null
            private set

        fun consumePendingDeepLink(): Int? {
            val id = pendingDeepLinkBookId
            pendingDeepLinkBookId = null
            return id
        }
    }
}