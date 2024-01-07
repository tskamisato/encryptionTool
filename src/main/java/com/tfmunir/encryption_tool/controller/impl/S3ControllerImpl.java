package com.tfmunir.encryption_tool.controller.impl;

import com.tfmunir.encryption_tool.controller.S3Controller;
import com.tfmunir.encryption_tool.service.IS3Service;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
public class S3ControllerImpl implements S3Controller {

    private final IS3Service s3Service;

    @Override
    public String deleteFile(MultipartFile file) {
        return s3Service.uploadFile(getAuthUsername(), file);
    }

    @Override
    public String downloadFile(String filename) {
        return s3Service.downloadFile(getAuthUsername(), filename);
    }

    @Override
    public String deleteFile(String filename) {
        return s3Service.deleteFile(getAuthUsername(), filename);
    }

    @Override
    public String renameFile(String oldFilename, String newFilename) {
        return s3Service.renameFile(getAuthUsername(), oldFilename, newFilename);
    }

    @Override
    public List<String> listUserFiles() {
        return s3Service.listUserFiles(getAuthUsername());
    }

    private String getAuthUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
