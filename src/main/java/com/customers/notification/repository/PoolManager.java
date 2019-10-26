package com.customers.notification.repository;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

public final class PoolManager {
    private static MySQLConnectOptions connectOptions = new MySQLConnectOptions()
            .setPort(InMemoryDBHandler.PORT)
            .setHost("localhost")
            .setDatabase(InMemoryDBHandler.DB)
            .setUser(InMemoryDBHandler.USER)
            .setPassword(InMemoryDBHandler.PASSWORD);
    private static PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    private static MySQLPool pool;

    public static MySQLPool getPool() {
        if (pool == null) {
            pool = MySQLPool.pool(connectOptions, poolOptions);
        }
        return pool;
    }
}
