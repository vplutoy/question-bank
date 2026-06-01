package com.example.questionbank.service;

import com.example.questionbank.entity.QuestionAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    QuestionAttachment upload(MultipartFile file) throws IOException;
    void deleteAttachment(Long attachmentId);
}
