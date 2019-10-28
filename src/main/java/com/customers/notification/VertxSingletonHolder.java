package com.customers.notification;

import io.vertx.core.Vertx;

public class VertxSingletonHolder {
    private static Vertx vertx;
    public static Vertx vertx() {
        if (vertx == null) {
            vertx = Vertx.vertx();
        }
        return vertx;
    }
}
