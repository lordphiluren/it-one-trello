package ru.sushchenko.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.sushchenko.trelloclone.config.KafkaConfig;
import ru.sushchenko.trelloclone.dto.kafka.AuthNotificationDto;
import ru.sushchenko.trelloclone.dto.kafka.UserHotTaskDto;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.service.KafkaMessagingService;
import ru.sushchenko.trelloclone.utils.mapper.KafkaMapper;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessagingServiceImpl implements KafkaMessagingService {
    private final KafkaConfig kafkaConfig;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaMapper kafkaMapper;
    @Override
    public void sendSignUpUser(User user) {
        AuthNotificationDto authNotificationDto = kafkaMapper.toAuthNotificationDto(user);
        kafkaTemplate.send(kafkaConfig.getAuthTopic(), authNotificationDto);
    }

    @Override
    public CompletableFuture<Void> sendUserHotTask(User user) {
        return CompletableFuture.runAsync(() -> {
            UserHotTaskDto userHotTaskDto = kafkaMapper.toHotTaskDto(user);
            kafkaTemplate.send(kafkaConfig.getNotificationTopic(), userHotTaskDto);
        });
    }
}
