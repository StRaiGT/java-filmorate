package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FeedTests {

    private final FeedStorage feedStorage;
    private final UserStorage userStorage;

    @Test
    @Order(1)
    public void testAddFeedAndGetFeedByUserId() {
        User user = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.now().minusYears(100))
                .build();
        userStorage.createUser(user);

        Feed feed = Feed.builder()
                .userId(1)
                .entityId(1)
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .build();

        feedStorage.addFeed(
                feed.getEntityId(),
                feed.getUserId(),
                feed.getTimestamp(),
                feed.getEventType(),
                feed.getOperation()
        );

        feed.setEventId(1);

        assertTrue(feedStorage.findByUserId(1).contains(feed));
    }

    @Test
    @Order(2)
    public void testGetFeedByIdWithoutEvents() {
        User user = User.builder()
                .email("email2@email.ru")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.now().minusYears(100))
                .build();
        userStorage.createUser(user);

        assertTrue(feedStorage.findByUserId(2).isEmpty());
    }
}
