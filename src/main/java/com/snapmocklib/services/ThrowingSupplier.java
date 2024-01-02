package com.snapmocklib.services;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Throwable;
}
