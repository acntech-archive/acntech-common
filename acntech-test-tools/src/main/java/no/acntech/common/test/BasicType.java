package no.acntech.common.test;

public interface BasicType<T> {

    boolean isType(Class<T> clazz);

    T getType(Class<T> clazz);
}
