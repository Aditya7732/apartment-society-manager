package com.society.manager.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String storeFile(MultipartFile file, String subDir);
    byte[] loadFileAsBytes(String filename);
    void deleteFile(String filename);
}
