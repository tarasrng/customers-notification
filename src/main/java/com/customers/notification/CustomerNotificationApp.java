package com.customers.notification;

import com.customers.notification.repository.InMemoryDBHandler;
import com.customers.notification.verticles.CustomerNotificationVerticle;
import com.customers.notification.verticles.CustomerProducerVerticle;
import com.customers.notification.verticles.HttpServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class CustomerNotificationApp {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Future<Void> dbPreparation = new InMemoryDBHandler().prepareDB();
        dbPreparation.compose(v -> {
            vertx.deployVerticle(HttpServerVerticle.class, new DeploymentOptions());
            vertx.deployVerticle(CustomerNotificationVerticle.class, new DeploymentOptions());
            vertx.deployVerticle(CustomerProducerVerticle.class, new DeploymentOptions());
            Promise<Void> promise = Promise.promise();
            promise.complete();
            return promise.future();
        });
    }
}
