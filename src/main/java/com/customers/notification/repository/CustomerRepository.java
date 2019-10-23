package com.customers.notification.repository;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {
    public AsyncResult<List<Customer>> getCustomers(int offset, int limit, SQLConnection connection) {
        List<Customer> customers = new ArrayList<>();
        Future<List<Customer>> result = Future.future();

        connection.query(String.format("SELECT * FROM customer %s", queryLimit(offset, limit)), res -> {
            if (res.succeeded()) {
                ResultSet rs = res.result();
                for (JsonArray row : rs.getResults()) {
                    customers.add(new Customer(row.getString(1), row.getString(2), row.getString(3), row.getString(4)));
                }
            }
            result.complete(customers);
        });
        return result;
    }

    public Customer getCustomerByName(String firstName, String lastName, SQLConnection connection) {
        return null;
    }

    private String queryLimit(int offset, int limit) {
        StringBuilder limitBuilder = new StringBuilder();
        if (limit > 0) {
            limitBuilder.append("LIMIT")
                    .append(offset > 0 ? String.format(" %d, ", offset) : " ")
                    .append(limit);
        }
        return limitBuilder.toString();
    }


}
