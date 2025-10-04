package tech.liax.fatec_2025.Services;
import io.minio.*;
import io.minio.errors.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.liax.fatec_2025.Entities.ImageEntity;
import tech.liax.fatec_2025.Entities.ProcessesImageEntity;
import tech.liax.fatec_2025.Repositories.ImageRepository;
import tech.liax.fatec_2025.Repositories.ProcessesImageRepository;
import tech.liax.fatec_2025.Utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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

    private final ProcessesImageRepository processesImageRepository;
    private final ImageRepository imageRepository;

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

    @Transactional
    public UUID upload(BufferedImage image) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            MinioClient minioClient = getMinioClient();
            ImageEntity newImage = imageRepository.save(new ImageEntity());
            newImage.setImagePath(newImage.getImageId().toString() + IMAGE_FORMAT);
            imageRepository.save(newImage);

            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(newImage.getImagePath()).stream(ImageUtil.convertImageToInputStream(image), -1, 10485760)
                            .contentType(CONTENT_TYPE)
                            .build()
            );



           return newImage.getImageId();
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }

        return null;
    }

    @Transactional
    public void saveProcessResult(ImageEntity originalImage, BufferedImage image, String processCode) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        UUID savedID = upload(image);
        ProcessesImageEntity entityToSave = ProcessesImageEntity.builder().image(originalImage).processCode(processCode).resultPath(savedID.toString() + ".png").build();
        processesImageRepository.save(entityToSave);
    }

    public BufferedImage getImageFile(UUID imageID) {
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

    public BufferedImage getImageFile(String resultPath) {
        try {
            MinioClient minioClient = getMinioClient();

            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucketName).object(resultPath).build()
            );

            return ImageIO.read(response);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            System.out.println("Error occurred: " + e);
        }

        return null;
    }
}