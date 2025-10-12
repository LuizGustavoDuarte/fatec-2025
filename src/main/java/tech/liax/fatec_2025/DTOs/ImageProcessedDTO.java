package tech.liax.fatec_2025.DTOs;

import tech.liax.fatec_2025.Utils.ProcessCodeEnum;

import java.awt.image.BufferedImage;

public record ImageProcessedDTO(BufferedImage processedImage, ProcessCodeEnum processCode) { }
