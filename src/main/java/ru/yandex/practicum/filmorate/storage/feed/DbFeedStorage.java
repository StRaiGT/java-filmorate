package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.map.FeedMap;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DbFeedStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FeedMap feedMap;

    @Override
    public List<Feed> findByUserId(int id) {

        String sql = "SELECT * FROM feed WHERE USER_ID = ? ORDER BY timestamp ASC";

        return jdbcTemplate.query(sql, feedMap, id);
    }

    @Override
    public void addFeed(int entityId, int userId, long timestamp, EventType eventType, Operation operation) {

        String sql = "INSERT INTO feed(ENTITY_ID, USER_ID, timestamp, EVENT_TYPE, OPERATION) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, entityId, userId, timestamp,
                eventType.toString(), operation.toString());
    }
}