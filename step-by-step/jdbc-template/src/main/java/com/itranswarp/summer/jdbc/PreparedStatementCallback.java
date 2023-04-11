package com.itranswarp.summer.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.annotation.Nullable;

@FunctionalInterface
public interface PreparedStatementCallback<T> {

    @Nullable
    T doInPreparedStatement(PreparedStatement ps) throws SQLException;

}
