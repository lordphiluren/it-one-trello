package ru.sushchenko.trelloclone.service;

import org.springframework.web.multipart.MultipartFile;
import ru.sushchenko.trelloclone.dto.attachments.AttachmentResponse;
import ru.sushchenko.trelloclone.entity.Task;

import java.util.List;

public interface AttachmentService {
    List<AttachmentResponse> addAttachmentsToTask(Task task, List<MultipartFile> attachments);
}
