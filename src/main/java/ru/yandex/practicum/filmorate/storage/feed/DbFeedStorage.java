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

        String sql = "SELECT * FROM FEED WHERE USER_ID = ? ORDER BY timestamp ASC";

        return jdbcTemplate.query(sql, this::makeFeed, id);
    }

    @Override
    public void addFeed(int entityId, int userId, long timestamp, EventType eventType, Operation operation) {

        String sql = "INSERT INTO FEED(ENTITY_ID, USER_ID, timestamp, EVENT_TYPE, OPERATION) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, entityId, userId, timestamp,
                eventType.toString(), operation.toString());
    }

    private Feed makeFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(rs.getLong("EVENT_ID"))
                .entityId(rs.getInt("ENTITY_ID"))
                .userId(rs.getInt("USER_ID"))
                .timestamp(rs.getLong("timestamp"))
                .eventType(EventType.valueOf(rs.getString("EVENT_TYPE")))
                .operation(Operation.valueOf(rs.getString("OPERATION")))
                .build();
    }
}