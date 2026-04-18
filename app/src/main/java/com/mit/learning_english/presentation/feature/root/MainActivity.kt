package com.mit.learning_english.presentation.feature.root

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.mit.learning_english.R
import com.mit.learning_english.shared.Constant
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var pendingDeepLinkManager: PendingDeepLinkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        // Khôi phục pending sau process death (nếu có) trước, rồi mới xử lý intent hiện tại
        lifecycleScope.launch {
            pendingDeepLinkManager.hydrateFromPersistent()
            handleDeepLinkIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        lifecycleScope.launch { handleDeepLinkIntent(intent) }
    }

    private suspend fun handleDeepLinkIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.action != Intent.ACTION_VIEW) return
        val uri = intent.data ?: return
        val bookId = extractBookId(uri) ?: return
        pendingDeepLinkManager.setPending(bookId)
    }

    private fun extractBookId(uri: Uri): Int? {
        // flulingo://book/{bookId}
        if (uri.scheme == Constant.DEEP_LINK_SCHEME && uri.host == Constant.DEEP_LINK_HOST_BOOK) {
            return uri.pathSegments?.firstOrNull()?.toIntOrNull()
        }
        // https://english.kimchimar3.store/book/{bookId}
        if (uri.scheme == "https" && uri.host == Constant.DEEP_LINK_HTTPS_HOST) {
            val segments = uri.pathSegments ?: return null
            if (segments.size >= 2 && segments[0] == Constant.DEEP_LINK_HOST_BOOK) {
                return segments[1].toIntOrNull()
            }
        }
        return null
    }
}
