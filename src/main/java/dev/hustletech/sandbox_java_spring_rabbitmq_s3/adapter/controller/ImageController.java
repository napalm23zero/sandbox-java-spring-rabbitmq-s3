package dev.hustletech.sandbox_java_spring_rabbitmq_s3.adapter.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.hustletech.sandbox_java_spring_rabbitmq_s3.adapter.service.ImageService;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @GetMapping("/fetch-and-save")
    public String fetchAndSaveImages() {
        List<CompletableFuture<Void>> futures = IntStream.range(0, 100)
                .mapToObj(i -> imageService.fetchAndSaveImage("https://picsum.photos/200/300.jpg", i))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return "Fetching and saving images...";
    }
}