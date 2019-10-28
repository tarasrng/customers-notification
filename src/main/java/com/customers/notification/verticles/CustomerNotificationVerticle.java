package com.customers.notification.verticles;

import com.customers.notification.notifier.CustomerNotifier;
import com.customers.notification.service.CustomerService;
import io.micronaut.context.BeanContext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class CustomerNotificationVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerNotificationVerticle.class);
    private CustomerService customerService;
    private CustomerNotifier notifier;

    public CustomerNotificationVerticle() {
        BeanContext beanContext = BeanContext.run();
        this.customerService = beanContext.getBean(CustomerService.class);
        this.notifier = beanContext.getBean(CustomerNotifier.class);
    }

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.setPeriodic(10000, id -> {
            customerService.getCustomers(0, 0)
                    .compose(customers -> {
                        customers.forEach(customer -> notifier.notify(customer));
                        Promise composePromise = Promise.promise();
                        composePromise.complete();
                        return composePromise.future();
                    });
        });
        LOGGER.info("Customer Notification Verticle deployed.");
        startPromise.complete();
    }
}
