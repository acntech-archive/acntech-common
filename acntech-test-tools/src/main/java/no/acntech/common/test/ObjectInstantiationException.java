package no.acntech.common.test;

public class ObjectInstantiationException extends TestException {

    public ObjectInstantiationException(Class<?> clazz) {
        super("Could not instantiate an object for class " + clazz.getName());
    }
}
