package domain

import result.DomainResult

interface ImageStorageService {
    suspend fun saveImage(fileBytes: ByteArray, filename: String): DomainResult<String>
    suspend fun deleteImage(imageUri: String): DomainResult<Unit>
}