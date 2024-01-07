package com.tfmunir.encryption_tool.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IS3Service {

    String uploadFile(String username, MultipartFile file);

    String downloadFile(String username, String filename);

    String deleteFile(String username, String filename);

    String renameFile(String username, String oldFilename, String newFilename);

    List<String> listUserFiles(String username);

}
