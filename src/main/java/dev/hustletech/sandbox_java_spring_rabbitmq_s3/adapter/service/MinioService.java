package dev.hustletech.sandbox_java_spring_rabbitmq_s3.adapter.service;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@Service
public class MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);

    @Value("${minio.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    public MinioService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @PostConstruct
    public void ensureBucketExists() {
        try {
            s3Client.headBucket(b -> b.bucket(bucketName));
            logger.info("Bucket exists: {}", bucketName);
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(b -> b.bucket(bucketName));
            logger.info("Created bucket: {}", bucketName);
        } catch (Exception e) {
            logger.error("Error checking/creating bucket {}: {}", bucketName, e.getMessage());
        }
    }

    public void uploadFile(Path filePath, Path dir) {
        String subDirName = dir.getFileName().toString();

        // Construct the S3 object key to include subdirectory
        String keyName = subDirName + "/" + filePath.getFileName().toString();

        try {
            s3Client.putObject(builder -> builder.bucket(bucketName).key(keyName).build(),
                    filePath);
            logger.info("Uploaded file to MinIO: {}", keyName);
        } catch (Exception e) {
            logger.error("Failed to upload file to MinIO {}: {}", keyName, e.getMessage());
        }
    }
}
