package tech.liax.fatec_2025.Services;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.liax.fatec_2025.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class ImageUploaderService {
    @Value("${spring.minio.host}")
    private String minioHostUrl;

    @Value("${spring.minio.accessKey}")
    private String accessKey;

    @Value("${spring.minio.secretKey}")
    private String secretKey;

    @Value("${spring.minio.bucketName}")
    private String bucketName;

    private final static String CONTENT_TYPE = "image/png";
    private final static String IMAGE_FORMAT = ".png";

    private MinioClient getMinioClient() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minioHostUrl)
                        .credentials(accessKey, secretKey)
                        .build();

        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

        if(!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        return minioClient;
    }

    public UUID upload(BufferedImage image) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            MinioClient minioClient = getMinioClient();

            UUID newUUID = UUID.randomUUID();

            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(newUUID + IMAGE_FORMAT).stream(ImageUtil.convertImageToInputStream(image), -1, 10485760)
                            .contentType(CONTENT_TYPE)
                            .build()
            );

           return newUUID;
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }

        return null;
    }

    public BufferedImage getOriginalFile(UUID imageID) {
        try {
            MinioClient minioClient = getMinioClient();

            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucketName).object(imageID + IMAGE_FORMAT).build()
            );

            return ImageIO.read(response);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("Error occurred: " + e);
        }

        return null;
    }
}