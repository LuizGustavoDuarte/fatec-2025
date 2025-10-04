package tech.liax.fatec_2025.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

public class ImageUtil {
    private static final String BASE64_REGEX = "^data:image/[^;]+;base64,";
    private static final String DEFAULT_IMAGE_FORMAT = "PNG";
    private static final String EMPTY_VALUE = "";

    public static BufferedImage decodeBase64ToImage(String base64String) throws IOException {
        String base64 = base64String.replaceFirst(BASE64_REGEX, EMPTY_VALUE);

        byte[] imageBytes = Base64.getDecoder().decode(base64);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        return ImageIO.read(byteArrayInputStream);
    }

    public static String decodeImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ImageIO.write(image, DEFAULT_IMAGE_FORMAT, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static InputStream convertImageToInputStream(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ImageIO.write(bufferedImage, DEFAULT_IMAGE_FORMAT, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return new ByteArrayInputStream(imageBytes);
    }

    public static BufferedImage stampImage (BufferedImage image) throws IOException {
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        graphics.drawImage(ImageIO.read(new File("src/main/java/tech/liax/fatec_2025/fatecLogo.png")),0,0, image.getWidth(), image.getHeight(), null);
        return image;
    }
}
