package com.customers.notification.verticles;

import com.customers.notification.provider.RandomCustomerProvider;
import com.customers.notification.repository.Customer;
import com.customers.notification.repository.PoolManager;
import com.customers.notification.service.CustomerService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.SqlConnection;

public class CustomerProducerVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerProducerVerticle.class);
    CustomerService customerService = new CustomerService();

    @Override
    public void start(Promise<Void> startPromise) {
        MySQLPool pool = PoolManager.getPool();
        vertx.setPeriodic(5000, id -> pool.getConnection(conn -> addCustomer(conn.result())));
        LOGGER.info("Customer Producer Verticle deployed.");
        startPromise.complete();
    }

    public void addCustomer(SqlConnection connection) {
        RandomCustomerProvider provider = new RandomCustomerProvider();
        Customer customer = provider.getCustomer();
        customerService.addCustomer(customer, connection).setHandler(result -> {
            if (result.succeeded()) {
                LOGGER.info(String.format("Customer %s %s with e-mail %s and number %s added!", customer.getFirstName(),
                        customer.getLastName(), customer.getEmail(), customer.getNumber()));
            } else {
                LOGGER.error("Can't add customer.", result.cause());
            }
        });
    }
}