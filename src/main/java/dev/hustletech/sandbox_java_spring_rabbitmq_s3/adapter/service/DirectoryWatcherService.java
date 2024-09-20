package dev.hustletech.sandbox_java_spring_rabbitmq_s3.adapter.service;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class DirectoryWatcherService {

    private static final Logger logger = LoggerFactory.getLogger(DirectoryWatcherService.class);

    @Value("${image.save.directory:/workspace/img}")
    private String mainImageSaveDirectory;

    private final MinioService minioService;

    private volatile boolean running = true;
    private WatchService watchService;

    public DirectoryWatcherService(MinioService minioService) {
        this.minioService = minioService;
    }

    @PostConstruct
    public void startWatching() {
        logger.info("Starting DirectoryWatcherService...");
        new Thread(() -> {
            try {
                watchDirectories();
            } catch (IOException | InterruptedException e) {
                logger.error("Error in DirectoryWatcherService: ", e);
            }
        }, "DirectoryWatcherThread").start();
    }

    private void watchDirectories() throws IOException, InterruptedException {
        watchService = FileSystems.getDefault().newWatchService();

        Map<WatchKey, Path> keyPathMap = new HashMap<>();

        // Define dirs to watch
        String[] subDirs = { "colored", "bw" };

        for (String subDir : subDirs) {
            Path dirPath = Paths.get(mainImageSaveDirectory, subDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                logger.info("Created directory: {}", dirPath);
            }

            if (Files.isDirectory(dirPath)) {
                WatchKey watchKey = dirPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
                keyPathMap.put(watchKey, dirPath);
                logger.info("Watching directory: {}", dirPath);
            } else {
                logger.error("Path is not a directory: {}", dirPath);
            }
        }

        while (running) {
            WatchKey key;
            try {
                // Wait for a key to be available
                key = watchService.take();
            } catch (ClosedWatchServiceException e) {
                logger.info("WatchService closed, stopping watcher.");
                break;
            }

            Path dir = keyPathMap.get(key);

            if (dir == null) {
                logger.warn("WatchKey not recognized!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // Overflow event
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    logger.warn("Overflow event detected!");
                    continue;
                }

                // Context
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                Path filePath = dir.resolve(fileName);

                logger.info("New file detected in {}: {}", dir, filePath);

                // Upload to MinIO using MinioService
                minioService.uploadFile(filePath, dir);

                // Delete after uploading
                try {
                    Files.delete(filePath);
                    logger.info("Deleted local file: {}", filePath);
                } catch (IOException e) {
                    logger.error("Failed to delete file {}: {}", filePath, e.getMessage());
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                keyPathMap.remove(key);
                logger.warn("WatchKey invalidated and removed: {}", dir);
                if (keyPathMap.isEmpty()) {
                    logger.warn("No more directories to watch. Exiting watcher.");
                    break;
                }
            }
        }

        watchService.close();
    }

    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        logger.info("Shutting down DirectoryWatcherService...");
        running = false;
        try {
            watchService.close();
        } catch (IOException e) {
            logger.error("Error closing WatchService: {}", e.getMessage());
        }
    }
}
