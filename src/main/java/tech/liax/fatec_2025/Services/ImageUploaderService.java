package tech.liax.fatec_2025.Services;

import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.liax.fatec_2025.DTOs.ImageProcessedDTO;
import tech.liax.fatec_2025.Entities.ImageEntity;
import tech.liax.fatec_2025.Entities.ProcessesImageEntity;
import tech.liax.fatec_2025.Repositories.ImageRepository;
import tech.liax.fatec_2025.Repositories.ProcessesImageRepository;
import tech.liax.fatec_2025.Utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static tech.liax.fatec_2025.Utils.ConstantsUtil.*;

@Service
@RequiredArgsConstructor
public class ImageUploaderService {
    private final ImageRepository imageRepository;
    private final ProcessesImageRepository processesImageRepository;
    private static final Logger logger = LoggerFactory.getLogger(ImageUploaderService.class);
    private MinioClient minioClient;

    @Value("${spring.minio.host}")
    private String minioHostUrl;

    @Value("${spring.minio.accessKey}")
    private String accessKey;

    @Value("${spring.minio.secretKey}")
    private String secretKey;

    @Value("${spring.minio.bucketName}")
    private String bucketName;

    @PostConstruct
    private void initMinioClient() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint(minioHostUrl)
                    .credentials(accessKey, secretKey)
                    .build();

            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            logger.error("Erro ao inicializar MinioClient: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao conectar ao MinIO", e);
        }
    }

    @Transactional
    public ImageEntity saveNewImageData() {
        try {
            ImageEntity newImageEntity = imageRepository.save(new ImageEntity());
            newImageEntity.setImagePath(newImageEntity.getImageId().toString() + DEFAULT_IMAGE_EXTENSION);
            imageRepository.save(newImageEntity);
            return newImageEntity;
        } catch (Exception e) {
            logger.error("Erro ao salvar imagem no banco: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao salvar imagem no banco", e);
        }
    }

    public void upload(BufferedImage image, String imagePath) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imagePath)
                            .stream(ImageUtil.convertImageToInputStream(image), DEFAULT_IMAGE_SIZE, DEFAULT_PART_SIZE)
                            .contentType(DEFAULT_CONTENT_TYPE)
                            .build()
            );
        } catch (Exception e) {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(imagePath)
                                .build()
                );
            } catch (Exception removeEx) {
                logger.error("Erro ao remover imagem do bucket: {}", removeEx.getMessage(), removeEx);
            }
            logger.error("Erro ao fazer upload da imagem: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao fazer upload da imagem", e);
        }
    }

    @Transactional
    public void saveProcessResult(ImageEntity originalImageEntity, List<ImageProcessedDTO> processedImages) {
        try {
            for(ImageProcessedDTO processedImageDTO : processedImages) {
                if(processedImageDTO == null || processedImageDTO.processedImage() == null) {
                    logger.warn("ProcessedImageDTO ou processedImage é nulo, pulando este item.");
                    continue;
                }

                ImageEntity newImageEntity = saveNewImageData();
                processesImageRepository.save(
                        ProcessesImageEntity.builder()
                                .mainImage(originalImageEntity)
                                .processedImage(newImageEntity)
                                .processCode(processedImageDTO.processCode())
                                .build()
                );
                upload(processedImageDTO.processedImage(), newImageEntity.getImagePath());
            }
        } catch (Exception e) {
            logger.error("Erro ao salvar resultado do processamento: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao salvar resultado do processamento", e);
        }
    }

    public Optional<BufferedImage> getImageFile(UUID imageID) {
        return getImageFile(imageID + DEFAULT_IMAGE_EXTENSION);
    }

    public Optional<BufferedImage> getImageFile(String resultPath) {
        try (GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(resultPath).build())) {
            BufferedImage image = ImageIO.read(response);
            return Optional.ofNullable(image);
        } catch (Exception e) {
            logger.warn("Imagem não encontrada ou erro ao ler: {}", e.getMessage());
            return Optional.empty();
        }
    }

}
