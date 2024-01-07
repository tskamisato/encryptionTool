package com.tfmunir.encryption_tool.service.impl;

import com.tfmunir.encryption_tool.repository.S3ClientRepository;
import com.tfmunir.encryption_tool.service.IS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements IS3Service {

    private final S3ClientRepository repository;

    @Override
    public String uploadFile(String username, MultipartFile file) {
        return repository.uploadFile(username, file);
    }

    @Override
    public String downloadFile(String username, String filename) {
        return repository.downloadFile(username, filename);
    }

    @Override
    public String deleteFile(String username, String filename) {
        return repository.deleteFile(username, filename);
    }

    @Override
    public String renameFile(String username, String oldFilename, String newFilename) {
        return repository.renameFile(username, oldFilename, newFilename);
    }

    @Override
    public List<String> listUserFiles(String username) {
        return repository.listUserFiles(username);
    }
}
