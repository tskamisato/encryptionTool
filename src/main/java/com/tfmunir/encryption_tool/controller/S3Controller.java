package com.tfmunir.encryption_tool.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping(value = "/api/tool", produces = MediaType.APPLICATION_JSON_VALUE)
public interface S3Controller {

    @PostMapping("/upload")
    String deleteFile(@RequestParam("file") MultipartFile file);

    @GetMapping("/download")
    String downloadFile(@RequestParam("filename") String filename);

    @DeleteMapping("/delete")
    String deleteFile(@RequestParam("filename") String filename);

    @PutMapping("/rename")
    String renameFile(@RequestParam("oldFilename") String oldFilename, @RequestParam("newFilename") String newFilename);

    @GetMapping("/list")
    List<String> listUserFiles();

}


