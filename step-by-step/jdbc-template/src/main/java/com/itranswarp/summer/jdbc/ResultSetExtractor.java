package com.itranswarp.summer.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.annotation.Nullable;

@FunctionalInterface
public interface ResultSetExtractor<T> {

    @Nullable
    T extractData(ResultSet rs) throws SQLException;

}
