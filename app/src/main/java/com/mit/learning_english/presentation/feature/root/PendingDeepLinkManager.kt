package com.mit.learning_english.presentation.feature.root

import com.mit.learning_english.data.local.datastore.PreferencesDatasource
import com.mit.learning_english.data.local.datastore.PreferencesKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Quản lý trạng thái "pending deep link" (id sách người nhận cần mở).
 *
 * Mục tiêu:
 *  - Sống sót qua process death: lưu vào DataStore.
 *  - Phát tán theo reactive pattern: expose StateFlow cho MainFragment observe.
 *  - Consumer duy nhất là MainFragment, và chỉ consume SAU KHI đã xác thực.
 *
 * Giá trị 0 hoặc không có key nghĩa là "không có pending".
 */
@Singleton
class PendingDeepLinkManager @Inject constructor(
    private val preferencesDatasource: PreferencesDatasource
) {

    private val _pendingBookId = MutableStateFlow<Int?>(null)
    val pendingBookId: StateFlow<Int?> = _pendingBookId.asStateFlow()

    /**
     * Observe pending từ DataStore (dùng ở tầng khởi động để khôi phục sau process death).
     */
    fun observePersisted(): Flow<Int?> =
        preferencesDatasource.getInteger(PreferencesKeys.PENDING_DEEP_LINK_BOOK_ID, 0)
            .map { if (it > 0) it else null }

    /**
     * Đặt pending sau khi nhận deep link. Ghi vào cả StateFlow (cho observer đang chạy)
     * và DataStore (để survive process death).
     */
    suspend fun setPending(bookId: Int) {
        if (bookId <= 0) return
        _pendingBookId.value = bookId
        preferencesDatasource.saveInteger(PreferencesKeys.PENDING_DEEP_LINK_BOOK_ID, bookId)
    }

    /**
     * Khởi tạo StateFlow từ DataStore tại thời điểm app startup.
     */
    suspend fun hydrateFromPersistent() {
        val persisted = preferencesDatasource
            .getInteger(PreferencesKeys.PENDING_DEEP_LINK_BOOK_ID, 0)
            .first()
        if (persisted > 0 && _pendingBookId.value == null) {
            _pendingBookId.value = persisted
        }
    }

    /**
     * Consume pending: trả về id và xoá cả in-memory lẫn persistent.
     * Chỉ nên gọi sau khi đã xác thực xong.
     */
    suspend fun consume(): Int? {
        val id = _pendingBookId.value
        if (id != null) {
            _pendingBookId.value = null
            preferencesDatasource.saveInteger(PreferencesKeys.PENDING_DEEP_LINK_BOOK_ID, 0)
        }
        return id
    }

    /**
     * Clear pending mà không consume (dùng khi user chủ động logout chẳng hạn).
     */
    suspend fun clear() {
        _pendingBookId.value = null
        preferencesDatasource.saveInteger(PreferencesKeys.PENDING_DEEP_LINK_BOOK_ID, 0)
    }
}
