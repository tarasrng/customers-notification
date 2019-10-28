package com.customers.notification;

import com.customers.notification.repository.InMemoryDBHandler;
import com.customers.notification.verticles.CustomerNotificationVerticle;
import com.customers.notification.verticles.CustomerProducerVerticle;
import com.customers.notification.verticles.HttpServerVerticle;
import io.micronaut.context.BeanContext;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class CustomerNotificationApp {
    public static void main(String[] args) {
        Vertx vertx = VertxSingletonHolder.vertx();
        Future<Void> dbPreparation = BeanContext.run().getBean(InMemoryDBHandler.class).prepareDB();
        dbPreparation.compose(v -> {
            vertx.deployVerticle(new HttpServerVerticle(), new DeploymentOptions());
            vertx.deployVerticle(new CustomerNotificationVerticle(), new DeploymentOptions());
            vertx.deployVerticle(new CustomerProducerVerticle(), new DeploymentOptions());
            Promise<Void> promise = Promise.promise();
            promise.complete();
            return promise.future();
        });
    }
}
