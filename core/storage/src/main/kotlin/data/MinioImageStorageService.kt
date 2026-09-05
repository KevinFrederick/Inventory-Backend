package data

import result.DomainResult
import result.ErrorType
import domain.ImageStorageService
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import java.io.ByteArrayInputStream
import java.util.UUID

class MinioImageStorageService(
    private val minioClient: MinioClient,
    private val publicBaseUrl: String,
    private val bucketName: String
): ImageStorageService {
    override suspend fun saveImage(fileBytes: ByteArray, filename: String): DomainResult<String> {
        return try {
            val inputStream = ByteArrayInputStream(fileBytes)

            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(filename)
                    .stream(inputStream, fileBytes.size.toLong(), -1)
                    .contentType("image/jpg")
                    .build(),
            )

            val cleanEndpoint = publicBaseUrl.removeSuffix("/")
            val cleanBucket = bucketName.removeSuffix("/").removePrefix("/")

            val url = "$cleanEndpoint/$cleanBucket/$filename"

            DomainResult.Success(url)
        } catch (e: Exception) {
            DomainResult.Error("MinIO Upload Failed: ${e.message}", ErrorType.UNKNOWN)
        }
    }

    override suspend fun deleteImage(imageUri: String): DomainResult<Unit> {
        return try {
            val objectName = imageUri.substringAfterLast("/")

            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .build()
            )

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error("MinIO Deletion Failed: ${e.message}", ErrorType.UNKNOWN)
        }
    }
}