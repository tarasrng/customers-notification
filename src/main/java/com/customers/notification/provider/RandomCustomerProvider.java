package com.customers.notification.provider;

import com.customers.notification.repository.Customer;

import java.util.Random;

public class RandomCustomerProvider {
    private static String[] firstNames = {"Ellen", "Olivia", "Henry", "David", "Mike", "John", "Ellie"};
    private static String[] lastNames = {"Smith", "Hall", "Wilson", "Cooper", "Doe", "Davis", "Brown"};
    private Random random = new Random();

    public Customer getCustomer() {
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        String email = firstName + "." + lastName + "@mail.com";
        String number = "+1" + (random.nextInt(99999999 - 10000000) + 10000000);
        return new Customer(firstName, lastName, email, number);
    }

}
