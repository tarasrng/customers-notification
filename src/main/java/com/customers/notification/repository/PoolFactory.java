package com.customers.notification.repository;

import com.customers.notification.VertxSingletonHolder;
import io.micronaut.context.annotation.Factory;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

import javax.inject.Singleton;

@Factory
public class PoolFactory {
    private static MySQLConnectOptions connectOptions = new MySQLConnectOptions()
            .setPort(InMemoryDBHandler.PORT)
            .setHost("localhost")
            .setDatabase(InMemoryDBHandler.DB)
            .setUser(InMemoryDBHandler.USER)
            .setPassword(InMemoryDBHandler.PASSWORD);
    private static PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);

    @Singleton
    MySQLPool createPool() {
        return MySQLPool.pool(VertxSingletonHolder.vertx(), connectOptions, poolOptions);
    }

    @Singleton
    Vertx createVertx() {
        return Vertx.vertx();
    }
}
