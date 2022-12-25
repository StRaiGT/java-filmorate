package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.storage.feed.EventType;
import ru.yandex.practicum.filmorate.storage.feed.Operation;

@Data
@Builder
public class Feed {
    private long timestamp;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private long eventId;
    private int entityId;
}