package com.customers.notification.service;

import com.customers.notification.repository.Customer;
import com.customers.notification.repository.CustomerRepository;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlConnection;

import java.util.List;

public class CustomerService {
    private CustomerRepository customerRepository = new CustomerRepository();

    public Future<List<Customer>> getCustomers(int offset, int limit, SqlConnection connection) {
        return customerRepository.getCustomers(offset, limit, connection);
    }

    public Future<Void> addCustomer(Customer customer, SqlConnection connection) {
        return customerRepository.addCustomer(customer, connection);
    }
}
