package tech.liax.fatec_2025.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.liax.fatec_2025.Entities.ImageEntity;
import tech.liax.fatec_2025.Entities.ProcessesImageEntity;
import tech.liax.fatec_2025.Repositories.ImageRepository;
import tech.liax.fatec_2025.Repositories.ProcessesImageRepository;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageUploaderService imageUploaderService;
    private final ImageRepository imageRepository;
    private final ProcessesImageRepository processesImageRepository;
    private final MessageSenderService messageSenderService;

    public UUID upload(BufferedImage image) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        UUID messageID = imageUploaderService.upload(image);
        messageSenderService.sendMessage(messageID.toString());
        return messageID;
    }

    public BufferedImage getImageFile(UUID imageID) {
        return imageUploaderService.getImageFile(imageID);
    }

    public BufferedImage getImageFile(String resultPath) {
        return imageUploaderService.getImageFile(resultPath);
    }

    public ImageEntity getImage(UUID imageID) {
        return imageRepository.findById(imageID).orElse(null);
    }

    public List<ProcessesImageEntity> getProcessingResults(UUID originalImageID) {
        ImageEntity foundImage = getImage(originalImageID);
        return foundImage == null ? new ArrayList<>() : foundImage.getProcessesImage();
    }
}
