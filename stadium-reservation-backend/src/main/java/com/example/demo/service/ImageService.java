package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    public String uploadImage(MultipartFile file) {
        String imageUrl = "/uploads/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        return imageUrl;
    }
}
