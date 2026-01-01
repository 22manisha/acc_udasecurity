package com.udacity.catpoint.image.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.stream.Collectors;


public class AwsImageService implements ImageService {

    private static final Logger logger = LoggerFactory.getLogger(AwsImageService.class);

    // AWS best practice recommends reusing a single client instance
    private static RekognitionClient client;

    public AwsImageService() {
        Properties configuration = new Properties();

        try (InputStream stream = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            configuration.load(stream);

        } catch (IOException ex) {
            logger.error("AWS Rekognition initialization failed due to missing configuration", ex);
            return;
        }

        AwsCredentials credentials = AwsBasicCredentials.create(
                configuration.getProperty("aws.id"),
                configuration.getProperty("aws.secret")
        );

        client = RekognitionClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(configuration.getProperty("aws.region")))
                .build();
    }

    /**
     * Determines whether the given image contains a cat based on confidence threshold.
     *
     * @param image image to analyze
     * @param confidenceThreshhold minimum confidence percentage
     * @return true if a cat is detected
     */
    @Override
    public boolean imageContainsCat(BufferedImage image, float confidenceThreshhold) {

        Image awsFormattedImage;

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            ImageIO.write(image, "jpg", outputStream);

            awsFormattedImage = Image.builder()
                    .bytes(SdkBytes.fromByteArray(outputStream.toByteArray()))
                    .build();

        } catch (IOException ex) {
            logger.error("Failed to convert image for AWS Rekognition", ex);
            return false;
        }

        DetectLabelsRequest request = DetectLabelsRequest.builder()
                .image(awsFormattedImage)
                .minConfidence(confidenceThreshhold)
                .build();

        DetectLabelsResponse result = client.detectLabels(request);

        logDetectedLabels(result);

        return result.labels()
                .stream()
                .anyMatch(label -> label.name().toLowerCase().contains("cat"));
    }

    private void logDetectedLabels(DetectLabelsResponse response) {
        logger.info(
                response.labels()
                        .stream()
                        .map(label -> String.format("%s(%.1f%%)", label.name(), label.confidence()))
                        .collect(Collectors.joining(", "))
        );
    }
}
