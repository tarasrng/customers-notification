package com.customers.notification.verticles;

import com.customers.notification.service.CustomerService;
import io.micronaut.context.BeanContext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class HttpServerVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
    private CustomerService customerService;

    public HttpServerVerticle() {
        BeanContext beanContext = BeanContext.run();
        this.customerService = beanContext.getBean(CustomerService.class);
    }

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json");
            customerService.getCustomers(0, 0)
                    .compose(customers -> {
                        response.end(Json.encode(customers));
                        Promise composePromise = Promise.promise();
                        composePromise.complete();
                        return composePromise.future();
                    });
        });

        server.requestHandler(router).listen(8080);
        LOGGER.info("HTTP Server started.");
        startPromise.complete();
    }
}
