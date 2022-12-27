package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DbFeedStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> findByUserId(int id) {

        final String sql = "SELECT * FROM FEED WHERE USER_ID = ? ORDER BY timestamp ASC";

        return jdbcTemplate.query(sql, this::makeFeed, id);
    }

    @Override
    public void addFeed(int entityId, int userId, long timestamp, EventType eventType, Operation operation) {

        final String sql = "INSERT INTO FEED(ENTITY_ID, USER_ID, timestamp, EVENT_TYPE, OPERATION) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, entityId, userId, timestamp,
                eventType.toString(), operation.toString());
    }

    private Feed makeFeed(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(resultSet.getLong("EVENT_ID"))
                .entityId(resultSet.getInt("ENTITY_ID"))
                .userId(resultSet.getInt("USER_ID"))
                .timestamp(resultSet.getLong("timestamp"))
                .eventType(EventType.valueOf(resultSet.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(resultSet.getString("OPERATION")))
                .build();
    }
}