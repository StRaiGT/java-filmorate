package ru.yandex.practicum.filmorate.map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.EventType;
import ru.yandex.practicum.filmorate.storage.feed.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedMap implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
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