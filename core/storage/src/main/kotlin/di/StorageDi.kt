package di

import data.MinioImageStorageService
import domain.ImageStorageService
import io.minio.MinioClient
import org.koin.dsl.module
import util.getSecret

val storageModule = module {
    single {
        MinioClient.builder()
            .endpoint(System.getenv("MINIO_URL") ?: "http://localhost:9000" )
            .credentials(
                System.getenv("MINIO_ROOT_USER") ?: "admin",
                getSecret("minio_pass","MINIO_ROOT_PASS"),
            )
            .build()
    }

    single<ImageStorageService> (createdAtStart = true) {
        MinioImageStorageService(
            minioClient = get(),
            publicBaseUrl = System.getenv("MINIO_PUBLIC_URL") ?: "http://localhost:9000",
            bucketName = "product"
        )
    }
}