package com.customers.notification.verticles;

import com.customers.notification.provider.RandomCustomerProvider;
import com.customers.notification.repository.Customer;
import com.customers.notification.service.CustomerService;
import io.micronaut.context.BeanContext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class CustomerProducerVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerProducerVerticle.class);
    private CustomerService customerService;

    public CustomerProducerVerticle() {
        BeanContext beanContext = BeanContext.run();
        this.customerService = beanContext.getBean(CustomerService.class);
    }

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.setPeriodic(5000, id -> addCustomer());
        LOGGER.info("Customer Producer Verticle deployed.");
        startPromise.complete();
    }

    public void addCustomer() {
        RandomCustomerProvider provider = new RandomCustomerProvider();
        Customer customer = provider.getCustomer();
        customerService.addCustomer(customer).setHandler(result -> {
            if (result.succeeded()) {
                LOGGER.info(String.format("Customer %s %s with e-mail %s and number %s added!", customer.getFirstName(),
                        customer.getLastName(), customer.getEmail(), customer.getNumber()));
            } else {
                LOGGER.error("Can't add customer.", result.cause());
            }
        });
    }
}