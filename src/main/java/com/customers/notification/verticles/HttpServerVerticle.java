package com.customers.notification.verticles;

import com.customers.notification.repository.PoolManager;
import com.customers.notification.service.CustomerService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLPool;

public class HttpServerVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
    private CustomerService customerService = new CustomerService();

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json");
            MySQLPool pool = PoolManager.getPool();
            pool.getConnection(res -> {
                if (res.succeeded()) {
                    customerService.getCustomers(0, 0, res.result())
                            .compose(customers -> {
                                response.end(Json.encode(customers));
                                Promise composePromise = Promise.promise();
                                composePromise.complete();
                                return composePromise.future();
                            });
                }
            });
        });

        server.requestHandler(router).listen(8080);
        LOGGER.info("HTTP Server started.");
        startPromise.complete();
    }
}
