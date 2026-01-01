package com.udacity.catpoint.image.service;

import java.awt.image.BufferedImage;

/**
 * Abstraction for image analysis services capable of
 * identifying whether an image contains a cat.
 */
public interface ImageService {

    boolean imageContainsCat(BufferedImage image, float confidenceThreshhold);

}
