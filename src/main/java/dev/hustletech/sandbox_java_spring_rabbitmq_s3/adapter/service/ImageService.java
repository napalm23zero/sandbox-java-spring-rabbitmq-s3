package dev.hustletech.sandbox_java_spring_rabbitmq_s3.adapter.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ImageService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${image.save.directory}")
    private String imageSaveDirectory;

    @Value("${rabbitmq.queue.colored}")
    private String coloredQueue;

    @Value("${rabbitmq.queue.bw}")
    private String bwQueue;

    @Async
    public CompletableFuture<Void> fetchAndSaveImage(String url, int index) {
        try {
            byte[] imageBytes = restTemplate.getForObject(url, byte[].class);
            rabbitTemplate.convertAndSend(coloredQueue, imageBytes);
            // saveImageToFile(imageBytes, "color", index);
            System.out.println("Sent colored image to queue: " + coloredQueue);

            byte[] bwImageBytes = convertToBlackAndWhite(imageBytes);
            rabbitTemplate.convertAndSend(bwQueue, bwImageBytes);
            // saveImageToFile(bwImageBytes, "bw", index);
            System.out.println("Sent B&W image to queue: " + bwQueue);

        } catch (Exception e) {
            System.out.println("Failed to fetch or save image: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    private byte[] convertToBlackAndWhite(byte[] imageBytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Thumbnails.of(bis)
                .size(200, 300)
                .outputFormat("jpg")
                .outputQuality(1.0)
                .imageType(BufferedImage.TYPE_BYTE_GRAY)
                .toOutputStream(bos);
        return bos.toByteArray();
    }

    private void saveImageToFile(byte[] imageBytes, String type, int index) throws IOException {
        File directory = new File(imageSaveDirectory + "/" + type);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        Path path = Path.of(directory.getAbsolutePath(), "image_" + index + ".jpg");
        Files.write(path, imageBytes, StandardOpenOption.CREATE);
        System.out.println("Saved " + type + " image to disk: image_" + index);
    }
}
