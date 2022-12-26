package ru.yandex.practicum.filmorate.service;

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
public class FeedService {

    private final FeedStorage feedStorage;

    public void add(int entityId, int userId,  EventType eventType, Operation operation) {
        long timestamp = Instant.now().toEpochMilli();
        feedStorage.addFeed(entityId, userId, timestamp, eventType, operation);
    }

    public List<Feed> getByUserId(int id) {
        return feedStorage.findByUserId(id);
    }
}
