package ru.yandex.practicum.filmorate.storage.feed;
import ru.yandex.practicum.filmorate.model.Feed;
import java.util.List;

public interface FeedStorage {
    List<Feed> findByUserId(int id);
    void addFeed(int entityId, int userId, long timestamp, EventType eventType, Operation operation);
}
