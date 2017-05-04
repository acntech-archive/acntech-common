package no.acntech.common.test;

public class ClassloaderNullException extends TestException {

    public ClassloaderNullException(Thread thread) {
        super("Thread " + thread.getName() + " gave a null classloader");
    }
}
