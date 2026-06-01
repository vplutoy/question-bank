package com.example.questionbank.controller;

import com.example.questionbank.entity.QuestionAttachment;
import com.example.questionbank.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public QuestionAttachment upload(@RequestParam("file") MultipartFile file) throws IOException {
        return fileService.upload(file);
    }

    @DeleteMapping("/attachments/{id}")
    public Map<String, Object> deleteAttachment(@PathVariable Long id) {
        fileService.deleteAttachment(id);
        return Map.of("success", true);
    }
}
