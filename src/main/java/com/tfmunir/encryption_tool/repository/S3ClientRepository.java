package com.tfmunir.encryption_tool.repository;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3ClientRepository {

    public static final String FILE_NOT_FOUND = "Fichero NO encontrado";

    String uploadFile(String username, MultipartFile file);

    String downloadFile(String username, String filename);

    String deleteFile(String username, String filename);

    String renameFile(String username, String oldFilename, String newFilename);

    List<String> listUserFiles(String username);
}
