package tech.liax.fatec_2025.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageUploaderService imageUploaderService;

    public UUID upload(BufferedImage image) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        return imageUploaderService.upload(image);
    }

    public BufferedImage getOriginalFile(UUID imageID) {
        return imageUploaderService.getOriginalFile(imageID);
    }
    public BufferedImage getStampedFile(UUID imageID) {
        return imageUploaderService.getOriginalFile(imageID);
    }
}
