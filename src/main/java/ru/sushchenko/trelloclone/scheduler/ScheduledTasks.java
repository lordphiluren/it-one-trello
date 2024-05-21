package ru.sushchenko.trelloclone.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.sushchenko.trelloclone.dto.kafka.UserHotTaskDto;
import ru.sushchenko.trelloclone.entity.User;
import ru.sushchenko.trelloclone.repo.UserRepo;
import ru.sushchenko.trelloclone.service.KafkaMessagingService;
import ru.sushchenko.trelloclone.utils.mapper.KafkaMapper;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final KafkaMessagingService kafkaMessagingService;
    private final UserRepo userRepo;

    @Scheduled(cron = "0 0 9 * * ?")
    public void reportUsersWithEndingTasks() {
        LocalDate today = LocalDate.now();
        ZonedDateTime startOfDay = today.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault());

        Set<User> users = userRepo.findUsersWithTasksDueToday(Date.from(startOfDay.toInstant()),
                Date.from(endOfDay.toInstant()));

        List<CompletableFuture<Void>> futures = users.stream()
                .map(kafkaMessagingService::sendUserHotTask)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("All user hot tasks sent to users with tasks due today.");
    }
}
