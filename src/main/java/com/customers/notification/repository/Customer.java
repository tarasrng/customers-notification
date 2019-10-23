package com.customers.notification.repository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {
    private String firstName;
    private String lastName;
    private String email;
    private String number;
}
