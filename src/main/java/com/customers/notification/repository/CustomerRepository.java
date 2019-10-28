package com.customers.notification.repository;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CustomerRepository {
    private MySQLPool pool;

    public CustomerRepository(MySQLPool pool) {
        this.pool = pool;
    }

    public Future<List<Customer>> getCustomers(int offset, int limit) {
        List<Customer> customers = new ArrayList<>();
        Promise<List<Customer>> result = Promise.promise();
        pool.getConnection(connectionAsyncResult -> {
            if (connectionAsyncResult.succeeded()) {
                SqlConnection connection = connectionAsyncResult.result();
                connection.query(String.format("SELECT * FROM customer %s", queryLimit(offset, limit)), select -> {
                    if (select.succeeded()) {
                        RowSet<Row> rs = select.result();
                        for (Row row : rs) {
                            customers.add(new Customer(row.getString(1),
                                    row.getString(2),
                                    row.getString(3),
                                    row.getString(4)));
                        }
                        result.complete(customers);
                    } else {
                        result.fail(select.cause());
                    }
                    connection.close();
                });
            }
        });
        return result.future();
    }

    public Future<Void> addCustomer(Customer customer) {
        Promise<Void> result = Promise.promise();
        pool.getConnection(connectionAsyncResult -> {
            if (connectionAsyncResult.succeeded()) {
                SqlConnection connection = connectionAsyncResult.result();
                connection.query(String.format("INSERT INTO customer VALUES (0, '%s', '%s', '%s', '%s')",
                        customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getNumber()), res -> {
                    if (res.succeeded()) {
                        result.complete();
                    } else {
                        result.fail(res.cause());
                    }
                    connection.close();
                });
            }
        });
        return result.future();
    }

    public Future<Void> createCustomerTable() {
        Promise<Void> result = Promise.promise();
        pool.getConnection(connectionAsyncResult -> {
            if (connectionAsyncResult.succeeded()) {
                SqlConnection connection = connectionAsyncResult.result();
                connection.query("CREATE TABLE customer(id int primary key AUTO_INCREMENT, first_name varchar(255), " +
                        "last_name varchar(255), email varchar(255), number varchar(20))", res -> {
                    if (res.succeeded()) {
                        result.complete();
                    } else {
                        result.fail(res.cause());
                    }
                    connection.close();
                });
            }
        });
        return result.future();
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
