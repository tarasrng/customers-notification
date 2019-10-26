package com.customers.notification.verticles;

import com.customers.notification.notifier.CustomerNotifier;
import com.customers.notification.notifier.EmailNotifier;
import com.customers.notification.repository.PoolManager;
import com.customers.notification.service.CustomerService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mysqlclient.MySQLPool;

public class CustomerNotificationVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerNotificationVerticle.class);
    private CustomerService customerService = new CustomerService();
    private CustomerNotifier notifier = new EmailNotifier();

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.setPeriodic(10000, id -> {
            MySQLPool pool = PoolManager.getPool();
            pool.getConnection(res -> {
                if (res.succeeded()) {
                    customerService.getCustomers(0, 0, res.result())
                            .compose(customers -> {
                                customers.forEach(customer -> notifier.notify(customer));
                                Promise composePromise = Promise.promise();
                                composePromise.complete();
                                return composePromise.future();
                            });
                }
            });

        });
        LOGGER.info("Customer Notification Verticle deployed.");
        startPromise.complete();
    }
}
