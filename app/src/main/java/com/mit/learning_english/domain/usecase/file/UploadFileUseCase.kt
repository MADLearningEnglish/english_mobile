package com.mit.learning_english.domain.usecase.file

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.mit.learning_english.data.remote.dto.FileDto
import com.mit.learning_english.domain.repository.FileRepository
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val repository: FileRepository
) {
    suspend operator fun invoke(uri: Uri, context: Context): Result<FileDto> = withContext(Dispatchers.IO) {
        return@withContext try {
            val file = getFileFromUri(uri, context)
            if (file == null) {
                return@withContext Result.Error("Cannot read file from uri")
            }
            
            // Lấy mime type
            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            
            val multipartBody = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestFile
            )

            val result = repository.uploadFile(multipartBody)
            
            // Xoá file tạm sau khi đã convert/upload
            file.delete()
            
            result
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error uploading file")
        }
    }

    private fun getFileFromUri(uri: Uri, context: Context): File? {
        val mimeType = context.contentResolver.getType(uri)
        val isImage = mimeType?.startsWith("image/") == true
        
        var fileName = "temp_file_${System.currentTimeMillis()}"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        }
        
        val tempFile = File(context.cacheDir, fileName)
        
        if (isImage) {
            compressImage(uri, tempFile, context)
            if (tempFile.exists() && tempFile.length() > 0) return tempFile
        }

        // Fallback for non-image types or if compress failed
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        return inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
            tempFile
        }
    }
    
    private fun compressImage(uri: Uri, outputFile: File, context: Context) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = android.graphics.BitmapFactory.Options()
            options.inJustDecodeBounds = true
            context.contentResolver.openInputStream(uri)?.use { 
                android.graphics.BitmapFactory.decodeStream(it, null, options)
            }

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 1024, 1024)
            options.inJustDecodeBounds = false

            // Decode bitmap with inSampleSize set
            val bitmap = context.contentResolver.openInputStream(uri)?.use { 
                android.graphics.BitmapFactory.decodeStream(it, null, options)
            }

            bitmap?.let {
                FileOutputStream(outputFile).use { out ->
                    it.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, out)
                }
                it.recycle()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateInSampleSize(options: android.graphics.BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
