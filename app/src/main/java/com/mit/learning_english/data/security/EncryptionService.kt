package com.mit.learning_english.data.security

/**
 * Interface cho dịch vụ mã hóa/giải mã dữ liệu
 * 
 * Sử dụng để mã hóa các dữ liệu nhạy cảm như token trước khi lưu vào storage
 */
interface EncryptionService {
    /**
     * Mã hóa một chuỗi plaintext thành chuỗi đã mã hóa (Base64)
     * 
     * @param plaintext Chuỗi cần mã hóa
     * @return Chuỗi đã mã hóa dạng Base64, hoặc null nếu có lỗi
     */
    suspend fun encrypt(plaintext: String): String?
    
    /**
     * Giải mã một chuỗi đã mã hóa về plaintext gốc
     * 
     * @param ciphertext Chuỗi đã mã hóa dạng Base64
     * @return Chuỗi plaintext gốc, hoặc null nếu có lỗi hoặc không thể giải mã
     */
    suspend fun decrypt(ciphertext: String): String?
}
