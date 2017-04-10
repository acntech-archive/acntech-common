package no.acntech.common.test;

public class NoSuchConstructorException extends TestException {

    public NoSuchConstructorException(Class<?> clazz) {
        super("Could not find matching constructor for class " + clazz.getName());
    }
}
