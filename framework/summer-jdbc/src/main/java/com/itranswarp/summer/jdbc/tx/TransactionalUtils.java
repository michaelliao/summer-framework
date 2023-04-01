package com.itranswarp.summer.jdbc.tx;

import java.sql.Connection;

import jakarta.annotation.Nullable;

public class TransactionalUtils {

    @Nullable
    public static Connection getCurrentConnection() {
        TransactionStatus ts = DataSourceTransactionManager.transactionStatus.get();
        return ts == null ? null : ts.connection;
    }
}
