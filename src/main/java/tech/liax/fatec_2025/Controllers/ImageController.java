package tech.liax.fatec_2025.Controllers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.liax.fatec_2025.DTOs.ImageUploadDTO;
import tech.liax.fatec_2025.Entities.ProcessesImageEntity;
import tech.liax.fatec_2025.Utils.ImageUtil;
import tech.liax.fatec_2025.Services.ImageService;
import tech.liax.fatec_2025.Utils.ProcessCodeEnum;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
public class ImageController {
    private final ImageService imageService;
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @PostMapping(value = "/upload/{processCode}")
    public ResponseEntity<String> uploadImage(@PathVariable int processCode, @RequestBody ImageUploadDTO request) {
        try {
            BufferedImage image = ImageUtil.decodeBase64ToImage(request.imageBase64());
            String imageID = imageService.upload(image, ProcessCodeEnum.fromCode(processCode));
            return ResponseEntity.ok(imageID);
        } catch (IllegalArgumentException e) {
            logger.warn("Process code inválido: {}", processCode, e);
            return ResponseEntity.badRequest().body("Código de processo inválido.");
        } catch (Exception e) {
            logger.error("Erro ao fazer upload da imagem", e);
            return ResponseEntity.status(500).body("Erro interno ao processar imagem.");
        }
    }

    @GetMapping("/get/{imageID}")
    public ResponseEntity<String> getImage(@PathVariable UUID imageID) {
        try {
            BufferedImage image = imageService.getImageFile(imageID);
            if (image == null) {
                return ResponseEntity.notFound().build();
            }
            String imageBase64 = ImageUtil.encodeImageToBase64(image);
            return ResponseEntity.ok(imageBase64);
        } catch (Exception e) {
            logger.error("Erro ao buscar imagem", e);
            return ResponseEntity.status(500).body("Erro interno ao buscar imagem.");
        }
    }

    @GetMapping("/get/{imageID}/processes")
    public ResponseEntity<List<String>> getProcessedImages(@PathVariable UUID imageID) {
        try {
            List<ProcessesImageEntity> foundProcesses = imageService.getProcessingResults(imageID);
            List<String> processedImages = foundProcesses.stream().map(
                    entity -> ImageUtil.encodeImageToBase64(
                            imageService.getImageFile(entity.getProcessedImage().getImagePath())
                    )).toList();
            return ResponseEntity.ok(processedImages);
        } catch (Exception e) {
            logger.error("Erro ao buscar imagens processadas", e);
            return ResponseEntity.status(500).body(null);
        }
    }

}
