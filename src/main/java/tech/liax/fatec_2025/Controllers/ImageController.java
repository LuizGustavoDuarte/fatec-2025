package tech.liax.fatec_2025.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.liax.fatec_2025.DTOs.ImageUploadDTO;
import tech.liax.fatec_2025.Entities.ProcessesImageEntity;
import tech.liax.fatec_2025.Utils.ImageUtil;
import tech.liax.fatec_2025.Services.ImageService;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
public class ImageController {
    private final ImageService imageService;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadImage(@RequestBody ImageUploadDTO request) {
        try {
            BufferedImage image = ImageUtil.decodeBase64ToImage(request.imageBase64());
            UUID imageID = imageService.upload(image);
            return ResponseEntity.ok(imageID.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/get/{imageID}")
    public ResponseEntity<String> getOriginalImage(@PathVariable UUID imageID) {
        try {
            BufferedImage image = imageService.getImageFile(imageID);
            String imageBase64 = ImageUtil.decodeImageToBase64(image);
            return ResponseEntity.ok(imageBase64);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/get/{imageID}/processed")
    public ResponseEntity<List<String>> getImage(@PathVariable UUID imageID) {
        try {
            List<ProcessesImageEntity> foundProcesses = imageService.getProcessingResults(imageID);
            List<String> processedImages = foundProcesses.stream().map( entity -> {
                try {
                    return ImageUtil.decodeImageToBase64(imageService.getImageFile(entity.getResultPath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
            return ResponseEntity.ok(processedImages);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}