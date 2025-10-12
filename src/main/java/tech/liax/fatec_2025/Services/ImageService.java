package tech.liax.fatec_2025.Services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tech.liax.fatec_2025.Entities.ImageEntity;
import tech.liax.fatec_2025.Entities.ProcessesImageEntity;
import tech.liax.fatec_2025.Exceptions.ImageNotFoundException;
import tech.liax.fatec_2025.Repositories.ImageRepository;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageUploaderService imageUploaderService;
    private final ImageRepository imageRepository;
    private final MessageSenderService messageSenderService;
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    public String upload(BufferedImage image) {
        ImageEntity imageEntity = imageUploaderService.saveNewImageData();
        imageUploaderService.upload(image, imageEntity.getImagePath());
        String imageID = imageEntity.getImageId().toString();
        messageSenderService.sendMessage(imageID);
        return imageID;
    }

    public BufferedImage getImageFile(UUID imageID) {
        return imageUploaderService.getImageFile(imageID)
                .orElseThrow(() -> {
                    logger.warn("Imagem não encontrada para o ID: {}", imageID);
                    return new ImageNotFoundException("Imagem não encontrada para o ID: " + imageID);
                });
    }

    public BufferedImage getImageFile(String resultPath) {
        return imageUploaderService.getImageFile(resultPath)
                .orElseThrow(() -> {
                    logger.warn("Imagem não encontrada para o caminho: {}", resultPath);
                    return new ImageNotFoundException("Imagem não encontrada para o caminho: " + resultPath);
                });
    }

    public ImageEntity getImage(UUID imageID) {
        return imageRepository.findById(imageID).orElse(null);
    }

    public List<ProcessesImageEntity> getProcessingResults(UUID originalImageID) {
        ImageEntity foundImage = getImage(originalImageID);
        return foundImage == null ? new ArrayList<>() : foundImage.getProcessesImage();
    }

}
