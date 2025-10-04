package tech.liax.fatec_2025.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.liax.fatec_2025.Minio.ImageUploader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    public ByteArrayInputStream decodeBase64ToImage(String base64String) throws IOException {
        String base64 = base64String.replaceFirst("^data:image/[^;]+;base64,", "");

        byte[] imageBytes = Base64.getDecoder().decode(base64);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        return byteArrayInputStream;
    }

    public String toBase64(ByteArrayOutputStream image) throws IOException {

        byte[] imageBytes = image.toByteArray();

        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public UUID upload(ByteArrayInputStream image) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        return ImageUploader.upload(image);
    }

    public ByteArrayOutputStream getOriginalFile(UUID imageID) throws IOException {
        return ImageUploader.getOriginalFile();
    }
    public ByteArrayOutputStream getStampedFile(UUID imageID) {
        return ImageUploader.getOriginalFile();
    }
}
