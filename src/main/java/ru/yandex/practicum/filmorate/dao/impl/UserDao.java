package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Qualifier("dbUserStorage")
public class UserDao implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public void resetId() {

    }

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        checkEmailExists(user.getEmail());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String query = new String(
                "INSERT INTO \"users\" (" +
                        "\"email\"," +
                        "\"login\"," +
                        "\"name\"," +
                        "\"birthday\") " +
                        "VALUES (?, ?, ?, ?)"
        );

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());

            String username = user.getName();
            if (username == null || username.isEmpty() || username.isBlank()) {
                ps.setString(3, user.getLogin());
                user.setName(user.getLogin());
            } else {
                ps.setString(3, username);
            }

            ps.setDate(4, Date.valueOf(user.getBirthday()));

            return ps;
        }, keyHolder);

        log.info("SQL execute: " + query + " with param " + user);

        user.setId(keyHolder.getKey().longValue());

        return user;
    }

    @Override
    public User updateUser(User user) {
        checkEmailExists(user.getEmail());

        String query = new String(
                "UPDATE \"users\" SET " +
                        "\"email\" = ?," +
                        "\"login\" = ?," +
                        "\"name\" = ?," +
                        "\"birthday\" = ? " +
                        "WHERE \"id\" = ?"
        );
        jdbcTemplate.update(
                query,
                user.getEmail(),
                user.getLogin(),
                user.getName() == null ? user.getLogin() : user.getName(),
                user.getBirthday(),
                user.getId()
        );

        log.info("SQL execute: " + query + " with param " + user);

        return user;
    }

    @Override
    public User getUserById(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM \"users\" WHERE \"id\" = ?",
                id
        );
        Set<Long> friendsSet = getFriendsList(id);

        if (userRows.next()) {
            User user = new User();

            user.setId(userRows.getLong("id"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("name"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
            user.setFriendsList(friendsSet);

            return user;
        } else {
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query(
                "SELECT * FROM \"users\"",
                (rs, rowNum) -> makeUser(rs)
        );
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT COUNT(*) \"count\" " +
                        "FROM \"friendships\" " +
                        "WHERE \"user_id\" = ? and \"friend_id\" = ?",
                userId,
                friendId
        );

        rs.next();
        if (rs.getLong("count") > 0)
            throw new ValidationException("Пользователь с ID "
                    + userId +
                    " уже отправил запрос дружбы пользователю "
                    + friendId
            );

        jdbcTemplate.update(
                "INSERT INTO \"friendships\" " +
                        "(\"user_id\", \"friend_id\") " +
                        "VALUES (?, ?)",
                userId,
                friendId
        );
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT COUNT(*) \"count\" " +
                        "FROM \"friendships\" " +
                        "WHERE \"user_id\" = ? and \"friend_id\" = ?",
                userId,
                friendId
        );

        rs.next();
        if (rs.getLong("count") == 0)
            throw new ValidationException("У пользователя с ID "
                    + userId +
                    " нет в друзьях пользователя "
                    + friendId
            );

        jdbcTemplate.update(
                "DELETE FROM \"friendships\" " +
                        "WHERE \"user_id\" = ? and \"friend_id\" = ?",
                userId,
                friendId
        );
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        user.setFriendsList(getFriendsList(user.getId()));

        return user;
    }

    private Set<Long> getFriendsList(Long id) {
        List<Long> friendsList = jdbcTemplate.query("SELECT * FROM \"friendships\" WHERE \"user_id\" = ?",
                (rs, rowNum) -> new Long(rs.getLong("friend_id")),
                id);

        return new HashSet<>(friendsList);
    }

    private void checkEmailExists(String email) {
        List<String> emailList = jdbcTemplate.query(
                "SELECT \"email\" FROM \"users\"",
                (rs, rowNum) -> rs.getString("email")
        );

        if (emailList.contains(email))
            throw new ValidationException("Current email exist...");
    }
}