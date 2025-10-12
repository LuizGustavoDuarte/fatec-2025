package tech.liax.fatec_2025.Services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tech.liax.fatec_2025.DTOs.ImageProcessedDTO;
import tech.liax.fatec_2025.Entities.ImageEntity;
import tech.liax.fatec_2025.Exceptions.ImageNotFoundException;
import tech.liax.fatec_2025.Utils.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsumerService {
    private final ImageUploaderService imageUploaderService;
    private final ImageService imageService;
    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @RabbitListener(queues = "${spring.rabbitmq.queueName}")
    public void receiveMessage(String message) {
        try {
            String[] messageParts = message.split(",");
            UUID imageID = UUID.fromString(messageParts[0]);
            ImageEntity originalImageEntity = imageService.getImage(imageID);
            BufferedImage imageToProcess = imageService.getImageFile(imageID);
            List<ImageProcessedDTO> processedImages = ImageUtil.processImage(imageToProcess);

            imageUploaderService.saveProcessResult(originalImageEntity, processedImages);
            logger.info("Processamento concluído para imagem: {}", imageID);
        } catch (ImageNotFoundException e) {
            logger.warn("Imagem não encontrada: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem: {}", e.getMessage(), e);
        }
    }

}
