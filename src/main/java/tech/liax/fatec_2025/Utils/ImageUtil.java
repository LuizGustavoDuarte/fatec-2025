package tech.liax.fatec_2025.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.liax.fatec_2025.Exceptions.ImageProcessingException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Objects;

import static tech.liax.fatec_2025.Utils.ConstantsUtil.*;

public class ImageUtil {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    public static BufferedImage decodeBase64ToImage(String base64String) {
        try {
            if (base64String == null || base64String.isEmpty()) {
                throw new ImageProcessingException("String Base64 vazia ou nula.");
            }
            String base64 = base64String.replaceFirst(BASE64_REGEX, EMPTY_VALUE);
            byte[] imageBytes = Base64.getDecoder().decode(base64);

            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes)) {
                BufferedImage image = ImageIO.read(byteArrayInputStream);
                if (image == null) throw new ImageProcessingException("Falha ao decodificar imagem.");
                return image;
            }
        } catch (Exception e) {
            logger.error("Erro ao decodificar Base64: {}", e.getMessage(), e);
            throw new ImageProcessingException("Erro ao decodificar Base64", e);
        }
    }

    public static String encodeImageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            if (image == null) throw new ImageProcessingException("Imagem nula.");
            ImageIO.write(image, DEFAULT_IMAGE_FORMAT, byteArrayOutputStream);
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            logger.error("Erro ao codificar imagem para Base64: {}", e.getMessage(), e);
            throw new ImageProcessingException("Erro ao codificar imagem para Base64", e);
        }
    }

    public static InputStream convertImageToInputStream(BufferedImage bufferedImage) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            if (bufferedImage == null) throw new ImageProcessingException("Imagem nula.");
            ImageIO.write(bufferedImage, DEFAULT_IMAGE_FORMAT, byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            logger.error("Erro ao converter imagem para InputStream: {}", e.getMessage(), e);
            throw new ImageProcessingException("Erro ao converter imagem para InputStream", e);
        }
    }

    public static BufferedImage processImage(BufferedImage image, ProcessCodeEnum processCode) {
        try {
            if (image == null || processCode == null) throw new ImageProcessingException("Parâmetros nulos.");
            if (processCode.equals(ProcessCodeEnum.FATEC_STAMP) || processCode.equals(ProcessCodeEnum.LIAX_STAMP)) {
                switch (processCode) {
                    case FATEC_STAMP -> stampImage(image, FATEC_LOGO_PATHNAME);
                    case LIAX_STAMP -> stampImage(image, LIAX_LOGO_PATHNAME);
                }
            }
            return image;
        } catch (Exception e) {
            logger.error("Erro ao processar imagem: {}", e.getMessage(), e);
            throw new ImageProcessingException("Erro ao processar imagem", e);
        }
    }

    private static void stampImage(BufferedImage image, String pathName) {
        try {
            Graphics2D graphics = image.createGraphics();
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, DEFAULT_ALPHA_VALUE));
            BufferedImage logo = ImageIO.read(Objects.requireNonNull(ImageUtil.class.getClassLoader().getResourceAsStream(DEFAULT_IMAGES_PATH + pathName)));
            if (logo == null) throw new ImageProcessingException("Logo não encontrada: " + pathName);
            graphics.drawImage(logo, ZERO_VALUE, ZERO_VALUE, image.getWidth(), image.getHeight(), null);
            graphics.dispose();
        } catch (Exception e) {
            logger.error("Erro ao aplicar stamp: {}", e.getMessage(), e);
            throw new ImageProcessingException("Erro ao aplicar stamp", e);
        }
    }

}
