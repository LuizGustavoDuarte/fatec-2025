package tech.liax.fatec_2025.Utils;

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.liax.fatec_2025.DTOs.ImageProcessedDTO;
import tech.liax.fatec_2025.Exceptions.ImageProcessingException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.bitwise_not;
import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;
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
        }

        return null;
    }

    public static List<ImageProcessedDTO> processImage(BufferedImage image) {
        List<ImageProcessedDTO> imagesProcessed = new ArrayList<>();
        try {
            if (image == null) throw new ImageProcessingException("Parâmetros nulos.");

            imagesProcessed.add(stampImage(image));
            imagesProcessed.add(cartoonFromBufferedImage(image));
            imagesProcessed.add(blurBackgroundFromBufferedImage(image));
        } catch (Exception e) {
            logger.error("Erro ao processar imagem: {}", e.getMessage(), e);
        }

        return imagesProcessed;
    }

    private static ImageProcessedDTO stampImage(BufferedImage image) {
        try {
            BufferedImage imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            Graphics2D graphics = imageCopy.createGraphics();
            graphics.drawImage(image, VALUE_ZERO, VALUE_ZERO, null);
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, DEFAULT_ALPHA_VALUE));

            BufferedImage logo = ImageIO.read(Objects.requireNonNull(ImageUtil.class.getClassLoader().getResourceAsStream(DEFAULT_IMAGES_PATH + LIAX_LOGO_PATHNAME)));
            if (logo == null) throw new ImageProcessingException("Logo da Liax não encontrada. ");
            graphics.drawImage(logo, VALUE_ZERO, VALUE_ZERO, imageCopy.getWidth(), imageCopy.getHeight(), null);
            graphics.dispose();
            return new ImageProcessedDTO(imageCopy, ProcessCodeEnum.STAMP);
        } catch (Exception e) {
            logger.error("Erro ao aplicar stamp: {}", e.getMessage(), e);
        }

        return null;
    }

    private static Mat bufferedImageToMat(BufferedImage image) {
        try {
            int width = image.getWidth();
            int height = image.getHeight();
            Mat src = new Mat(height, width, opencv_core.CV_8UC3);

            byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

            if (image.getType() == BufferedImage.TYPE_3BYTE_BGR) {
                src.data().put(data);
            } else {
                BufferedImage convertedImg = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                convertedImg.getGraphics().drawImage(image, VALUE_ZERO, VALUE_ZERO, null);
                byte[] convertedData = ((DataBufferByte) convertedImg.getRaster().getDataBuffer()).getData();
                src.data().put(convertedData);
            }

            return src;
        } catch (Exception e) {
            logger.error("Erro ao converter BufferedImage para Mat: {}", e.getMessage(), e);
        }

        return null;
    }

    private static ImageProcessedDTO matToImageProcessedDTO(Mat src, ProcessCodeEnum processCode) {
        try {
            int type = BufferedImage.TYPE_BYTE_GRAY;
            if (src.channels() > VALUE_ONE) {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }

            int bufferSize = src.channels() * src.cols() * src.rows();
            byte[] buffer = new byte[bufferSize];
            src.data().get(buffer);

            BufferedImage image = new BufferedImage(src.cols(), src.rows(), type);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(buffer, VALUE_ZERO, targetPixels, VALUE_ZERO, buffer.length);

            return new ImageProcessedDTO(image, processCode);
        } catch (Exception e) {
            logger.error("Erro ao converter Mat para DTO: {}", e.getMessage(), e);
        }

        return null;
    }

    private static Mat cartoonize(Mat src) {
        try {
            Mat imgColor = new Mat();
            opencv_imgproc.bilateralFilter(src, imgColor, VALUE_NINE, VALUE_SEVENTY_FIVE, VALUE_SEVENTY_FIVE);

            Mat imgGray = new Mat();
            opencv_imgproc.cvtColor(src, imgGray, opencv_imgproc.COLOR_BGR2GRAY);

            Mat imgBlur = new Mat();
            opencv_imgproc.medianBlur(imgGray, imgBlur, VALUE_SEVEN);

            Mat edges = new Mat();
            opencv_imgproc.adaptiveThreshold(imgBlur, edges, RGB_WHITE_COLOR,
                    opencv_imgproc.ADAPTIVE_THRESH_MEAN_C,
                    opencv_imgproc.THRESH_BINARY, VALUE_NINE, VALUE_TWO);

            Mat edgesColor = new Mat();
            opencv_imgproc.cvtColor(edges, edgesColor, opencv_imgproc.COLOR_GRAY2BGR);

            Mat cartoon = new Mat();
            opencv_core.bitwise_and(imgColor, edgesColor, cartoon);

            return cartoon;
        } catch(Exception e) {
            logger.error("Erro ao cartoonizar imagem: {}", e.getMessage(), e);
        }

        return null;
    }

    private static Mat blurBackground(Mat src) {
        try {
            Mat mask = new Mat(src.size(), CV_8UC1, new Scalar(VALUE_ZERO));

            int centerX = src.cols() / VALUE_TWO;
            int centerY = src.rows() / VALUE_TWO;
            int width = src.cols() / VALUE_THREE;
            int height = src.rows() / VALUE_THREE;

            Rect roi = new Rect(centerX - width / VALUE_TWO, centerY - height / VALUE_TWO, width, height);

            Mat submask = mask.rowRange(roi.y(), roi.y() + roi.height())
                    .colRange(roi.x(), roi.x() + roi.width());

            UByteRawIndexer indexer = submask.createIndexer();
            for (int y = VALUE_ZERO; y < submask.rows(); y++) {
                for (int x = VALUE_ZERO; x < submask.cols(); x++) {
                    indexer.put(y, x, RGB_WHITE_COLOR);
                }
            }
            indexer.release();

            Mat blurred = new Mat();
            GaussianBlur(src, blurred, new Size(VALUE_TWENTY_ONE, VALUE_TWENTY_ONE), VALUE_ZERO);

            Mat result = new Mat();
            src.copyTo(result, mask);
            Mat invertedMask = new Mat();
            bitwise_not(mask, invertedMask);
            blurred.copyTo(result, invertedMask);

            return result;
        } catch (Exception e) {
            logger.error("Erro ao desfocar fundo da imagem: {}", e.getMessage(), e);
        }

        return null;
    }

    private static ImageProcessedDTO cartoonFromBufferedImage(BufferedImage image) {
        Mat matInput = bufferedImageToMat(image);
        Mat matCartoon = cartoonize(matInput);
        return matToImageProcessedDTO(matCartoon, ProcessCodeEnum.CARTOONIZE);
    }

    private static ImageProcessedDTO blurBackgroundFromBufferedImage(BufferedImage image) {
        Mat matInput = bufferedImageToMat(image);
        Mat matBlurBackground = blurBackground(matInput);
        return matToImageProcessedDTO(matBlurBackground, ProcessCodeEnum.BLUR);
    }

}
