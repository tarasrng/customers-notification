package com.customers.notification.notifier;

import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;

@Factory
public class NotifierFactory {
    @Singleton
    CustomerNotifier notifier() {
        return new EmailNotifier();
    }
}
