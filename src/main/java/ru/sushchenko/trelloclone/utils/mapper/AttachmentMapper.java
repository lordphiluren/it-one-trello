package ru.sushchenko.trelloclone.utils.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.sushchenko.trelloclone.dto.attachments.AttachmentResponse;
import ru.sushchenko.trelloclone.entity.TaskAttachment;

@Component
@RequiredArgsConstructor
public class AttachmentMapper {
    private final ModelMapper modelMapper;
    public AttachmentResponse toDto(TaskAttachment attachment) {
        return modelMapper.map(attachment, AttachmentResponse.class);
    }
}
