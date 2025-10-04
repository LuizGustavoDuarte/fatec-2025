package tech.liax.fatec_2025.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tech.liax.fatec_2025.Config.RabbitMQConfig;
import tech.liax.fatec_2025.Entities.ImageEntity;
import tech.liax.fatec_2025.Utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsumerService {
    private final ImageUploaderService imageUploaderService;
    private final ImageService imageService;
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        UUID messageUUID = UUID.fromString(message);
        ImageEntity originalImageEntity = imageService.getImage(messageUUID);
        BufferedImage imageToProcess = imageUploaderService.getImageFile(messageUUID);
        BufferedImage stampedImage = ImageUtil.stampImage(imageToProcess);
        imageUploaderService.saveProcessResult(originalImageEntity, stampedImage, "stamp");
    }
}
