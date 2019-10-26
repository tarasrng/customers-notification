package com.customers.notification.repository;

import com.customers.notification.provider.RandomCustomerProvider;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.SchemaConfig;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.SqlConnection;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.config.SchemaConfig.aSchemaConfig;
import static com.wix.mysql.distribution.Version.v5_6_21;

public class InMemoryDBHandler {
    public static final int PORT = 3306;
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String DB = "customers";

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDBHandler.class);
    private CustomerRepository customerRepository = new CustomerRepository();
    private RandomCustomerProvider provider = new RandomCustomerProvider();

    public Future<Void> prepareDB() {
        MySQLPool pool = PoolManager.getPool();
        Promise<Void> promise = Promise.promise();
        try {
            anEmbeddedMysql(getMySqlConfig()).addSchema(getSchemaConfig()).start();
        } catch (Exception e) {
            LOGGER.error("Could not start DB server", e);
            promise.fail(e);
            return promise.future();
        }
        Handler<AsyncResult<SqlConnection>> insertFuture = conn -> customerRepository.addCustomer(provider.getCustomer(),
                conn.result()).setHandler(insert -> {
            if (insert.succeeded()) {
                LOGGER.info("DB Initialized.");
                promise.complete();
            } else {
                LOGGER.error("Can't insert a customer.", insert.cause());
                promise.fail(insert.cause());
            }
        });
        Handler<AsyncResult<SqlConnection>> createFuture = conn -> customerRepository.createCustomerTable(conn.result()).setHandler(create -> {
            if (create.succeeded()) {
                pool.getConnection(insertFuture);
            } else {
                LOGGER.error("Can't create a table.", create.cause());
                promise.fail(create.cause());
            }
        });
        pool.getConnection(createFuture);
        return promise.future();
    }

    private SchemaConfig getSchemaConfig() {
        return aSchemaConfig(DB)
                .build();
    }

    private MysqldConfig getMySqlConfig() {
        return aMysqldConfig(v5_6_21)
                .withCharset(UTF8)
                .withPort(PORT)
                .withUser(USER, PASSWORD)
                .build();
    }
}
