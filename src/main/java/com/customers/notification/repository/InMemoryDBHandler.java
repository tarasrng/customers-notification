package com.customers.notification.repository;

import com.customers.notification.provider.RandomCustomerProvider;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.SchemaConfig;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.inject.Singleton;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.config.SchemaConfig.aSchemaConfig;
import static com.wix.mysql.distribution.Version.v5_6_21;

@Singleton
public class InMemoryDBHandler {
    public static final int PORT = 3306;
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String DB = "customers";

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDBHandler.class);

    private CustomerRepository customerRepository;
    private RandomCustomerProvider provider = new RandomCustomerProvider();

    public InMemoryDBHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Future<Void> prepareDB() {
        Promise<Void> promise = Promise.promise();
        try {
            anEmbeddedMysql(getMySqlConfig()).addSchema(getSchemaConfig()).start();
            LOGGER.info("DB server started");
        } catch (Exception e) {
            LOGGER.error("Could not start DB server", e);
            promise.fail(e);
            return promise.future();
        }

        customerRepository.createCustomerTable().setHandler(create -> {
            if (create.succeeded()) {
                LOGGER.info("Table created.");
                customerRepository.addCustomer(provider.getCustomer()).setHandler(insert -> {
                    if (insert.succeeded()) {
                        LOGGER.info("First customer inserted.");
                        promise.complete();
                    } else {
                        LOGGER.error("Can't insert a customer.", insert.cause());
                        promise.fail(insert.cause());
                    }
                });
            } else {
                LOGGER.error("Can't create a table.", create.cause());
                promise.fail(create.cause());
            }
        });
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
