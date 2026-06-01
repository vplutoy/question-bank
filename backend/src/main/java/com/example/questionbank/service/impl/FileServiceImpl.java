package com.example.questionbank.service.impl;

import com.example.questionbank.entity.QuestionAttachment;
import com.example.questionbank.mapper.QuestionAttachmentMapper;
import com.example.questionbank.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final QuestionAttachmentMapper attachmentMapper;
    private final Path uploadDir;

    public FileServiceImpl(QuestionAttachmentMapper attachmentMapper,
                           @Value("${file.upload.path:./uploads}") String uploadPath) {
        this.attachmentMapper = attachmentMapper;
        this.uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录", e);
        }
    }

    @Override
    public QuestionAttachment upload(MultipartFile file) throws IOException {
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Path targetDir = uploadDir.resolve(dateDir);
        Files.createDirectories(targetDir);

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + ext;

        Path targetPath = targetDir.resolve(storedName);
        file.transferTo(targetPath.toFile());

        String relativePath = dateDir + "/" + storedName;

        QuestionAttachment attachment = new QuestionAttachment();
        attachment.setFileName(originalName);
        attachment.setFilePath(relativePath);
        attachment.setFileType("QUESTION_IMAGE");
        attachmentMapper.insert(attachment);

        return attachment;
    }

    @Override
    public void deleteAttachment(Long attachmentId) {
        QuestionAttachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment != null) {
            Path filePath = uploadDir.resolve(attachment.getFilePath());
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException ignored) {
            }
            attachmentMapper.deleteById(attachmentId);
        }
    }
}
