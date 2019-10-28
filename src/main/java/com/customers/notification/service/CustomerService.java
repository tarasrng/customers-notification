package com.customers.notification.service;

import com.customers.notification.repository.Customer;
import com.customers.notification.repository.CustomerRepository;
import io.vertx.core.Future;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class CustomerService {
    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Future<List<Customer>> getCustomers(int offset, int limit) {
        return customerRepository.getCustomers(offset, limit);
    }

    public Future<Void> addCustomer(Customer customer) {
        return customerRepository.addCustomer(customer);
    }
}
