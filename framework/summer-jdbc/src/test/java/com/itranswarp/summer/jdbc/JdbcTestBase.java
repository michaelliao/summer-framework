package com.itranswarp.summer.jdbc;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;

import com.itranswarp.summer.io.PropertyResolver;

public class JdbcTestBase {

    public static final String CREATE_USER = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(255) NOT NULL, age INTEGER)";
    public static final String CREATE_ADDRESS = "CREATE TABLE addresses (id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER NOT NULL, address VARCHAR(255) NOT NULL, zip INTEGER)";

    public static final String INSERT_USER = "INSERT INTO users (name, age) VALUES (?, ?)";
    public static final String INSERT_ADDRESS = "INSERT INTO addresses (userId, address, zip) VALUES (?, ?, ?)";

    public static final String UPDATE_USER = "UPDATE users SET name = ?, age = ? WHERE id = ?";
    public static final String UPDATE_ADDRESS = "UPDATE addresses SET address = ?, zip = ? WHERE id = ?";

    public static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    public static final String DELETE_ADDRESS_BY_USERID = "DELETE FROM addresses WHERE userId = ?";

    public static final String SELECT_USER = "SELECT * FROM users WHERE id = ?";
    public static final String SELECT_USER_NAME = "SELECT name FROM users WHERE id = ?";
    public static final String SELECT_USER_AGE = "SELECT age FROM users WHERE id = ?";
    public static final String SELECT_ADDRESS_BY_USERID = "SELECT * FROM addresses WHERE userId = ?";

    @BeforeEach
    public void beforeEach() {
        cleanDb();
    }

    public PropertyResolver createPropertyResolver() {
        var ps = new Properties();
        ps.put("summer.datasource.url", "jdbc:sqlite:test.db");
        ps.put("summer.datasource.username", "sa");
        ps.put("summer.datasource.password", "");
        ps.put("summer.datasource.driver-class-name", "org.sqlite.JDBC");
        var pr = new PropertyResolver(ps);
        return pr;
    }

    void cleanDb() {
        Path db = Path.of("test.db").normalize().toAbsolutePath();
        try {
            Files.deleteIfExists(db);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
