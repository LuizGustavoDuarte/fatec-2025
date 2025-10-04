package tech.liax.fatec_2025.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.liax.fatec_2025.DTOs.ImageUploadDTO;
import tech.liax.fatec_2025.Services.ImageService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/image")
public class ImageController {
    private final ImageService imageService;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadImage(@RequestBody ImageUploadDTO request) {
        try {
            ByteArrayInputStream image = imageService.decodeBase64ToImage(request.imageBase64());
            UUID imageID = imageService.upload(image);
            return ResponseEntity.ok(imageID.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/get/{photoID}")
    public ResponseEntity<String> getOriginalImage(@PathVariable UUID photoID) {
        try {
            ByteArrayOutputStream image = imageService.getOriginalFile(photoID);
            String imageBase64 = imageService.toBase64(image);
            return ResponseEntity.ok(imageBase64);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/get/{photoID}/stamped")
    public ResponseEntity<String> getImage(@PathVariable UUID photoID) {
        try {
            ByteArrayOutputStream image = imageService.getStampedFile(photoID);
            String imageBase64 = imageService.toBase64(image);
            return ResponseEntity.ok(imageBase64);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}