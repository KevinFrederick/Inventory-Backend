package di

import data.MinioImageStorageService
import domain.ImageStorageService
import io.minio.MinioClient
import org.koin.dsl.module

val storageModule = module {
    single {
        MinioClient.builder()
            .endpoint(System.getenv("MINIO_URL") ?: "http://localhost:9000" )
            .credentials(
                System.getenv("MINIO_ROOT_USER") ?: "admin",
                System.getenv("MINIO_ROOT_PASS") ?: "password",
            )
            .build()
    }

    single<ImageStorageService> {
        MinioImageStorageService(
            minioClient = get(),
            publicBaseUrl = System.getenv("MINIO_URL") ?: "http://localhost:9000",
            bucketName = "product"
        )
    }
}