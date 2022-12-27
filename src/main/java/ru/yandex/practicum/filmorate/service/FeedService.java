package ru.yandex.practicum.filmorate.service;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.enums.Operation;


import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final FeedStorage feedStorage;

    public void add(int entityId, int userId,  EventType eventType, Operation operation) {
        log.info("Добавление в ленту операции {} типа {} от пользователя с id {}", operation, eventType, userId);
        long timestamp = Instant.now().toEpochMilli();
        feedStorage.addFeed(entityId, userId, timestamp, eventType, operation);
    }

    public List<Feed> getByUserId(int id) {
        log.info("Вывод пользователя с id {}", id);
        return feedStorage.findByUserId(id);
    }
}