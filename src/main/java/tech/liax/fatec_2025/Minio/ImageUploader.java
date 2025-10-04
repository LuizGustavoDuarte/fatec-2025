package tech.liax.fatec_2025.Minio;
import io.minio.*;
import io.minio.errors.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ImageUploader {
    public static UUID upload(ByteArrayInputStream image)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient = getMinioClient();

            minioClient.putObject(
                    PutObjectArgs.builder().bucket("asiatrip").object("imagemdahora.jpg").stream(
                                    image, -1, 10485760)
                            .contentType("image/jpeg")
                            .build());

           return UUID.randomUUID();
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }

        return UUID.randomUUID();
    }

    private static MinioClient getMinioClient() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("http://localhost:9000")
                        .credentials("fatec123", "fatec123")
                        .build();

        // Make 'asiatrip' bucket if not exist.
        boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket("asiatrip").build());
        if (!found) {
            // Make a new bucket called 'asiatrip'.
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("asiatrip").build());
        } else {
            System.out.println("Bucket 'asiatrip' already exists.");
        }

        return minioClient;
    }

    public static ByteArrayOutputStream getOriginalFile() {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient = getMinioClient();

            ByteArrayInputStream bucketImage = new ByteArrayInputStream(minioClient.getObject(
                    GetObjectArgs.builder().bucket("asiatrip").object("imagemdahora.jpg").build()
            ).readAllBytes());

            ByteArrayOutputStream image = new ByteArrayOutputStream();
            image.writeBytes(bucketImage.readAllBytes());
            return image;
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("Error occurred: " + e);
        }

        return null;
    }
}