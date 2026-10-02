package di

import data.MinioImageStorageService
import domain.ImageStorageService
import io.ktor.server.config.ApplicationConfig
import io.minio.MinioClient
import org.koin.dsl.module
import util.getSecret

fun storageModule (config: ApplicationConfig) = module {
    single {
        MinioClient.builder()
            .endpoint(config.property("minio.url").getString())
            .credentials(
                config.property("minio.accessKey").getString(),
                getSecret("minio_pass","MINIO_ROOT_PASS"),
            )
            .build()
    }

    single<ImageStorageService> (createdAtStart = true) {
        MinioImageStorageService(
            minioClient = get(),
            publicBaseUrl = config.propertyOrNull("minio.publicUrl")?.getString() ?: "http://localhost:9000",
            bucketName = "product"
        )
    }
}