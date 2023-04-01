package com.itranswarp.summer.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.annotation.Nullable;

@FunctionalInterface
public interface ConnectionCallback<T> {

    @Nullable
    T doInConnection(Connection con) throws SQLException;

}
