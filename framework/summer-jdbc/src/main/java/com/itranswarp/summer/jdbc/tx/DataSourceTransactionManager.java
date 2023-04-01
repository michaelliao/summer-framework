package com.itranswarp.summer.jdbc.tx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.itranswarp.summer.exception.DataAccessException;
import com.itranswarp.summer.exception.TransactionException;

public class DataSourceTransactionManager implements PlatformTransactionManager, InvocationHandler {

    static final ThreadLocal<TransactionStatus> transactionStatus = new ThreadLocal<>();

    final DataSource dataSource;

    public DataSourceTransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TransactionStatus ts = transactionStatus.get();
        if (ts == null) {
            // start new transaction:
            try (Connection connection = dataSource.getConnection()) {
                final boolean autoCommit = connection.getAutoCommit();
                if (autoCommit) {
                    connection.setAutoCommit(false);
                }
                try {
                    ts = new TransactionStatus(connection);
                    transactionStatus.set(ts);
                    Object r = method.invoke(proxy, args);
                    connection.commit();
                    return r;
                } catch (Throwable e) {
                    TransactionException te = new TransactionException(e);
                    try {
                        connection.rollback();
                    } catch (SQLException sqle) {
                        te.addSuppressed(new DataAccessException(sqle));
                    }
                    throw te;
                } finally {
                    transactionStatus.remove();
                    if (autoCommit) {
                        connection.setAutoCommit(true);
                    }
                }
            }
        } else {
            // join current transaction:
            return method.invoke(proxy, args);
        }
    }

}
