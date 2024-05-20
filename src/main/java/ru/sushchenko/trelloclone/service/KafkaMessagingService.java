package ru.sushchenko.trelloclone.service;

import ru.sushchenko.trelloclone.entity.User;

import java.util.concurrent.CompletableFuture;

public interface KafkaMessagingService {
    void sendSignUpUser(User user);

    CompletableFuture<Void> sendUserHotTask(User user);
}
