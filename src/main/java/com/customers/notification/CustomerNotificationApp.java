package com.customers.notification;

import com.customers.notification.repository.CustomerRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import org.h2.tools.Server;

import java.sql.SQLException;

public class CustomerNotificationApp {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        CustomerRepository repo = new CustomerRepository();
        Vertx vertx = Vertx.vertx();
        prepareDB(vertx, result -> {
            final JDBCClient client = JDBCClient.createShared(vertx, new JsonObject()
                    .put("url", "jdbc:h2:tcp://localhost/~/customers")
                    .put("driver_class", "org.h2.Driver")
                    .put("max_pool_size", 30)
                    .put("user", "sa")
                    .put("password", ""));
            client.getConnection(res -> {
                if (res.succeeded()) {
                    repo.getCustomers(10, 0, res.result());
                }
            });
        });

    }

    private static Handler<AsyncResult> prepareDB(Vertx vertx, Handler<AsyncResult> handler) throws SQLException, ClassNotFoundException {
        Server.createTcpServer("-tcpAllowOthers").start();
        JDBCClient client = JDBCClient.createShared(vertx, new JsonObject()
                .put("url", "jdbc:h2:mem:customers")
                .put("driver_class", "org.h2.Driver")
                .put("max_pool_size", 30)
                .put("user", "sa")
                .put("password", ""));

        client.getConnection(conn -> {
            if (conn.failed()) {
                System.err.println(conn.cause().getMessage());
                return;
            }
            final SQLConnection connection = conn.result();

            connection.execute("CREATE TABLE customer(id int primary key, first_name varchar(255), last_name varchar(255), " +
                    "email varchar(255), number varchar(20))", create -> {
                if (create.failed()) {
                    System.err.println("Cannot create the table");
                    create.cause().printStackTrace();
                    handler.handle(create);
                    return;
                }

                // insert some test data
                connection.execute("INSERT INTO customer VALUES (1, 'John', 'Doe', 'jdoe@mail.com', '+123456789'), " +
                        "(2, 'Bob', 'Martin', 'bmartin@mail.com', '+123456789'), (3, 'Steve', 'Roberts', 'sroberts@mail.com', '+123456789')", insert -> {
                    handler.handle(insert);
                });
            });
        });
        return handler;
    }
}
