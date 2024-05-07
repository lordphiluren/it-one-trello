package ru.sushchenko.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.sushchenko.trelloclone.dto.attachments.AttachmentResponse;
import ru.sushchenko.trelloclone.entity.Task;
import ru.sushchenko.trelloclone.entity.TaskAttachment;
import ru.sushchenko.trelloclone.repo.AttachmentRepo;
import ru.sushchenko.trelloclone.service.AttachmentService;
import ru.sushchenko.trelloclone.service.UploadService;
import ru.sushchenko.trelloclone.utils.mapper.AttachmentMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepo attachRepo;
    private final UploadService uploadService;
    private final AttachmentMapper attachMapper;
    @Override
    @Transactional
    public List<AttachmentResponse> addAttachmentsToTask(Task task, List<MultipartFile> attachments) {
        Set<String> urls = uploadAttachments(attachments);
        List<TaskAttachment> savedAttachments = attachRepo.saveAll(createAttachmentsFromUrls(urls, task));
        log.info("{} attachments saved for task with id: {}", savedAttachments.size(), task.getId());
        return savedAttachments.stream()
                .map(attachMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachmentResponse> getAttachmentsByTaskId(UUID taskId) {
        return attachRepo.findByTaskId(taskId).stream()
                .map(attachMapper::toDto)
                .collect(Collectors.toList());
    }

    private Set<TaskAttachment> createAttachmentsFromUrls(Set<String> urls, Task task) {
        return urls.stream().map(url -> new TaskAttachment(null, url, task)).collect(Collectors.toSet());
    }
    private Set<String> uploadAttachments(List<MultipartFile> attachments) {
        Set<String> urls = new HashSet<>();
        ResponseEntity<Set<String>> response = uploadService.upload(attachments);

        if (response.getStatusCode().is2xxSuccessful()) {
            Set<String> responseBody = response.getBody();
            if (responseBody != null) {
                urls.addAll(responseBody);
            }
        } else {
            log.error("Cannot upload photos. Upload service returned status {}", response.getStatusCode());
        }
        return urls;
    }
}
