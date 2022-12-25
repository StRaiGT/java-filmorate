package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DbReviewStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Review> createReview(Review review) {
            final String sqlQuery = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) " +
                    "VALUES (?, ?, ?, ?);";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
                        ps.setString(1, review.getContent());
                        ps.setBoolean(2, review.getIsPositive());
                        ps.setInt(3, review.getUserId());
                        ps.setInt(4, review.getFilmId());
                        return ps;
                    },
                    keyHolder
            );
            review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            return Optional.of(review);
        }

    @Override
    public Optional<Review> getReviewById(int id) {
        final String sqlQuery = "SELECT  *, IFNULL(R.COUNT_LIKE, 0) - IFNULL(R.COUNT_DISLAIKE, 0) AS USEFUL " +
                "FROM (SELECT R.*, L.COUNT_LIKE, L.COUNT_DISLAIKE " +
                "FROM REVIEWS AS R " +
                "LEFT JOIN (SELECT LR.REVIEW_ID AS REVIEW_ID, COUNT(USER_ID) AS COUNT_LIKE, COUNT_DISLAIKE " +
                "FROM LIKES_REVIEWS AS LR " +
                "LEFT JOIN (SELECT DL.REVIEW_ID, COUNT(USER_ID) AS COUNT_DISLAIKE " +
                "FROM DISLIKE_REVIEWS AS DL " +
                "GROUP BY DL.REVIEW_ID " +
                ") AS DL ON LR.REVIEW_ID = DL.REVIEW_ID " +
                "GROUP BY LR.REVIEW_ID " +
                "UNION " +
                "SELECT DL.REVIEW_ID AS REVIEW_ID, COUNT_LIKE, COUNT(USER_ID) AS COUNT_DISLAIKE " +
                "FROM DISLIKE_REVIEWS AS DL " +
                "LEFT JOIN (SELECT LR.REVIEW_ID, COUNT(USER_ID) AS COUNT_LIKE " +
                "FROM LIKES_REVIEWS AS LR " +
                "GROUP BY LR.REVIEW_ID " +
                ") AS LR ON DL.REVIEW_ID = LR.REVIEW_ID " +
                "GROUP BY DL.REVIEW_ID) AS L ON R.REVIEW_ID = L.REVIEW_ID) AS R " +
                "WHERE REVIEW_ID = ?";
        try {
            return Optional.of(jdbcTemplate.query(sqlQuery, this::makeReview, id).get(0));
        } catch (IndexOutOfBoundsException exception) {
            throw new NotFoundException("Review not found");
        }
    }

    @Override
    public Collection<Review> getReviews(int filmId, int count) {
        if (filmId == -1) {
            final String sqlQuery = "SELECT  *, IFNULL(R.COUNT_LIKE, 0) - IFNULL(R.COUNT_DISLAIKE, 0) AS USEFUL " +
                    "FROM (SELECT R.*, L.COUNT_LIKE, L.COUNT_DISLAIKE " +
                    "FROM REVIEWS AS R " +
                    "LEFT JOIN (SELECT LR.REVIEW_ID AS REVIEW_ID, COUNT(USER_ID) AS COUNT_LIKE, COUNT_DISLAIKE " +
                    "FROM LIKES_REVIEWS AS LR " +
                    "LEFT JOIN (SELECT DL.REVIEW_ID, COUNT(USER_ID) AS COUNT_DISLAIKE " +
                    "FROM DISLIKE_REVIEWS AS DL " +
                    "GROUP BY DL.REVIEW_ID " +
                    ") AS DL ON LR.REVIEW_ID = DL.REVIEW_ID " +
                    "GROUP BY LR.REVIEW_ID " +
                    "UNION " +
                    "SELECT DL.REVIEW_ID AS REVIEW_ID, COUNT_LIKE, COUNT(USER_ID) AS COUNT_DISLAIKE " +
                    "FROM DISLIKE_REVIEWS AS DL " +
                    "LEFT JOIN (SELECT LR.REVIEW_ID, COUNT(USER_ID) AS COUNT_LIKE " +
                    "FROM LIKES_REVIEWS AS LR " +
                    "GROUP BY LR.REVIEW_ID " +
                    ") AS LR ON DL.REVIEW_ID = LR.REVIEW_ID " +
                    "GROUP BY DL.REVIEW_ID) AS L ON R.REVIEW_ID = L.REVIEW_ID) AS R " +
                    "ORDER BY  USEFUL DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sqlQuery, this::makeReview, count);
        } else {
            final String sqlQuery = "SELECT  *, IFNULL(R.COUNT_LIKE, 0) - IFNULL(R.COUNT_DISLAIKE, 0) AS USEFUL " +
                    "FROM (SELECT R.*, L.COUNT_LIKE, L.COUNT_DISLAIKE " +
                    "FROM REVIEWS AS R " +
                    "LEFT JOIN (SELECT LR.REVIEW_ID AS REVIEW_ID, COUNT(USER_ID) AS COUNT_LIKE, COUNT_DISLAIKE " +
                    "FROM LIKES_REVIEWS AS LR " +
                    "LEFT JOIN (SELECT DL.REVIEW_ID, COUNT(USER_ID) AS COUNT_DISLAIKE " +
                    "FROM DISLIKE_REVIEWS AS DL " +
                    "GROUP BY DL.REVIEW_ID " +
                    ") AS DL ON LR.REVIEW_ID = DL.REVIEW_ID " +
                    "GROUP BY LR.REVIEW_ID " +
                    "UNION " +
                    "SELECT DL.REVIEW_ID AS REVIEW_ID, COUNT_LIKE, COUNT(USER_ID) AS COUNT_DISLAIKE " +
                    "FROM DISLIKE_REVIEWS AS DL " +
                    "LEFT JOIN (SELECT LR.REVIEW_ID, COUNT(USER_ID) AS COUNT_LIKE " +
                    "FROM LIKES_REVIEWS AS LR " +
                    "GROUP BY LR.REVIEW_ID " +
                    ") AS LR ON DL.REVIEW_ID = LR.REVIEW_ID " +
                    "GROUP BY DL.REVIEW_ID) AS L ON R.REVIEW_ID = L.REVIEW_ID) AS R " +
                    "WHERE FILM_ID = ? " +
                    "ORDER BY  USEFUL DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sqlQuery, this::makeReview, filmId, count);
        }
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        final String sqlQuery = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ?" +
                " WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public void addLikeReview(int id, int userId) {
        final String sqlQueryFilm = "INSERT INTO LIKES_REVIEWS (REVIEW_ID, USER_ID)" +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQueryFilm, id, userId);
    }

    @Override
    public void addDislikeReview(int id, int userId) {
        final String sqlQueryFilm = "INSERT INTO DISLIKE_REVIEWS (REVIEW_ID, USER_ID)" +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQueryFilm, id, userId);
    }

    @Override
    public Boolean removeLikeReview(int id, int userId) {
        final String sqlQuery = "DELETE FROM LIKES_REVIEWS WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        return true;
    }

    @Override
    public Boolean removeDislikeReview(int id, int userId) {
        final String sqlQuery = "DELETE FROM DISLIKE_REVIEWS WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
        return true;
    }

    @Override
    public Boolean removeReview(int id) {
        jdbcTemplate.update("DELETE FROM REVIEWS WHERE REVIEW_ID = ?", id);
        return true;
    }

    private Review makeReview(ResultSet resultSet, int rowNum) throws SQLException {
        return new Review(
                resultSet.getInt("REVIEW_ID"),
                resultSet.getString("CONTENT"),
                String.valueOf(resultSet.getBoolean("IS_POSITIVE")),
                resultSet.getInt("USER_ID"),
                resultSet.getInt("FILM_ID"),
                resultSet.getInt("USEFUL"));
    }
}
