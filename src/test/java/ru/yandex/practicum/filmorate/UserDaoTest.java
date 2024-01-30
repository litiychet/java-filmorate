package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.impl.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.Assert.*;

@JdbcTest
@Sql({"/schema.sql", "/data.sql"})
public class UserDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setEmail("test1@yandex.ru");
        user1.setLogin("testlogin1");
        user1.setName("testname1");
        user1.setBirthday(LocalDate.of(1995, 12, 29));

        user2 = new User();
        user2.setEmail("test2@yandex.ru");
        user2.setLogin("testlogin2");
        user2.setName("testname2");
        user2.setBirthday(LocalDate.of(2020, 1, 1));
    }

    @Test
    public void createUser() {
        UserDao userDao = new UserDao(jdbcTemplate);

        Long id = userDao.createUser(user1).getId();
        User createdUser = userDao.getUserById(id);

        assertEquals(createdUser.getLogin(), user1.getLogin());
        assertEquals(createdUser.getEmail(), user1.getEmail());
        assertEquals(createdUser.getName(), user1.getName());
        assertEquals(createdUser.getBirthday(), user1.getBirthday());
    }

    @Test
    public void updateUser() {
        UserDao userDao = new UserDao(jdbcTemplate);

        Long id = userDao.createUser(user1).getId();

        user2.setId(id);

        userDao.updateUser(user2);
        User newUser = userDao.getUserById(id);

        assertEquals(newUser.getLogin(), user2.getLogin());
        assertEquals(newUser.getEmail(), user2.getEmail());
        assertEquals(newUser.getName(), user2.getName());
        assertEquals(newUser.getBirthday(), user2.getBirthday());
    }

    @Test
    public void getUserById() {
        UserDao userDao = new UserDao(jdbcTemplate);

        Long id = userDao.createUser(user1).getId();
        User getUser = userDao.getUserById(id);

        assertEquals(getUser.getLogin(), user1.getLogin());
        assertEquals(getUser.getEmail(), user1.getEmail());
        assertEquals(getUser.getName(), user1.getName());
        assertEquals(getUser.getBirthday(), user1.getBirthday());
    }

    @Test
    public void getUsers() {
        UserDao userDao = new UserDao(jdbcTemplate);

        userDao.createUser(user1);
        userDao.createUser(user2);

        assertEquals(2, userDao.getUsers().size());
    }

    @Test
    public void addFriend() {
        UserDao userDao = new UserDao(jdbcTemplate);

        Long id1 = userDao.createUser(user1).getId();
        Long id2 = userDao.createUser(user2).getId();

        userDao.addFriend(id1, id2);

        user1 = userDao.getUserById(id1);
        user2 = userDao.getUserById(id2);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT COUNT(*) \"count\" " +
                        "FROM \"friendships\" "
        );

        rs.next();

        assertFalse(user1.getFriendsList().isEmpty());
        assertEquals(1, rs.getLong("count"));
    }

    @Test
    public void removeFriend() {
        UserDao userDao = new UserDao(jdbcTemplate);

        Long id1 = userDao.createUser(user1).getId();
        Long id2 = userDao.createUser(user2).getId();

        userDao.addFriend(id1, id2);

        userDao.removeFriend(id1, id2);

        user1 = userDao.getUserById(id1);
        user2 = userDao.getUserById(id2);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT COUNT(*) \"count\" " +
                        "FROM \"friendships\" "
        );

        rs.next();

        assertEquals(0, rs.getLong("count"));
        Assertions.assertTrue(user1.getFriendsList().isEmpty());
    }
}
