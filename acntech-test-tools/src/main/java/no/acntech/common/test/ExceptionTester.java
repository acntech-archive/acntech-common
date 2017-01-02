package no.acntech.common.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public final class ExceptionTester {

    /**
     * Test exceptions.
     *
     * @param throwables Exceptions to test.
     * @throws IllegalAccessException    If access control denied access to constructor of exception.
     * @throws InstantiationException    If constructor of exception is from an abstract class.
     * @throws InvocationTargetException If constructor of exception throws exception.
     */
    @SafeVarargs
    public static void testExceptions(Class<? extends Throwable>... throwables) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (throwables == null) {
            throw new IllegalArgumentException("Input is null");
        }

        for (Class<? extends Throwable> throwable : throwables) {
            testException(throwable);
        }
    }

    public static void testException(Class<? extends Throwable> throwable) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (throwable == null) {
            throw new IllegalArgumentException("Input is null");
        }

        Constructor<? extends Throwable> constructor = TestReflectionUtils.findConstructorWithParameters(throwable);
        testException(constructor, throwable);
        constructor = TestReflectionUtils.findConstructorWithParameters(throwable, String.class);
        testException(constructor, throwable, "Exception message");
        constructor = TestReflectionUtils.findConstructorWithParameters(throwable, Throwable.class);
        testException(constructor, throwable, new Throwable("Exception cause"));
        constructor = TestReflectionUtils.findConstructorWithParameters(throwable, String.class, Throwable.class);
        testException(constructor, throwable, "Exception message", new Throwable("Exception cause"));
    }

    private static void testException(Constructor<? extends Throwable> constructor, Class<? extends Throwable> throwable,
                                      Object... args) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (constructor != null) {
            Throwable t = TestReflectionUtils.createBean(constructor, args);
            try {
                throwException(t);

                fail("Exception was not thrown");
            } catch (Throwable e) {
                assertThat("Exception is not of expected type", e, instanceOf(throwable));
            }
        }
    }

    private static void throwException(Throwable t) throws Throwable {
        throw t;
    }
}
