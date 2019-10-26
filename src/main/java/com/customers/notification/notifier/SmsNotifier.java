package com.customers.notification.notifier;

import com.customers.notification.repository.Customer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class SmsNotifier implements CustomerNotifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsNotifier.class);

    @Override
    public void notify(Customer customer) {
        LOGGER.info(String.format("Customer %s %s was notified by phone %s!", customer.getFirstName(),
                customer.getLastName(), customer.getNumber()));
    }
}