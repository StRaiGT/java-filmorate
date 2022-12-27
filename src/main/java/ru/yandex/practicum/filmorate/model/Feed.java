package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class Feed {
    private long eventId;
    private int entityId;
    private int userId;
    private long timestamp;
    private EventType eventType;
    private Operation operation;
}