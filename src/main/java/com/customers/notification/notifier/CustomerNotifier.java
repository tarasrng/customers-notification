package com.customers.notification.notifier;

import com.customers.notification.repository.Customer;

public interface CustomerNotifier {
    void notify(Customer customer);
}
