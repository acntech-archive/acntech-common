package no.acntech.common.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public final class ExceptionTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionTester.class);

    private ExceptionTester() {
    }

    /**
     * Too generic method name.
     *
     * @deprecated use {@link #testExceptions(Class[])} ()} instead.
     */
    @Deprecated
    public static void test(Class<? extends Throwable>... throwables) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        testExceptions(throwables);
    }

    /**
     * Test array of exceptions.
     *
     * @param throwables Exceptions to test.
     * @throws IllegalAccessException    If access control denied access to constructor of exception.
     * @throws InstantiationException    If constructor of exception is from an abstract class.
     * @throws InvocationTargetException If constructor of exception throws exception.
     */
    @SafeVarargs
    public static void testExceptions(Class<? extends Throwable>... throwables) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (throwables == null) {
            throw new IllegalArgumentException("Input throwables array is null");
        }

        for (Class<? extends Throwable> throwable : throwables) {
            testException(throwable);
        }
    }

    /**
     * Test single exception.
     *
     * @param throwable Exception to test.
     * @throws IllegalAccessException    If access control denied access to constructor of exception.
     * @throws InstantiationException    If constructor of exception is from an abstract class.
     * @throws InvocationTargetException If constructor of exception throws exception.
     */
    public static void testException(Class<? extends Throwable> throwable) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (throwable == null) {
            throw new IllegalArgumentException("Input is null");
        }

        testExceptionWithArgs(throwable);
        testExceptionWithArgs(throwable, "Exception message");
        testExceptionWithArgs(throwable, new Throwable("Exception cause"));
        testExceptionWithArgs(throwable, "Exception message", new Throwable("Exception cause"));
    }

    /**
     * Test single exception.
     *
     * @param throwable Exception to test.
     * @param args      Constructor arguments for the exception to test.
     * @throws IllegalAccessException    If access control denied access to constructor of exception.
     * @throws InstantiationException    If constructor of exception is from an abstract class.
     * @throws InvocationTargetException If constructor of exception throws exception.
     */
    public static void testExceptionWithArgs(Class<? extends Throwable> throwable, Object... args) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (throwable == null) {
            throw new IllegalArgumentException("Input throwable is null");
        }

        if (args == null) {
            throw new IllegalArgumentException("Input args array is null");
        }

        Class<?>[] params = TestTypeFactory.getClassesForObjects(args);

        Constructor<? extends Throwable> constructor = TestReflectionUtils.findConstructorWithAllParamsMatch(throwable, params);
        testException(constructor, throwable, args);
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
        } else {
            LOGGER.warn("Constructor is null");
        }
    }

    private static void throwException(Throwable t) throws Throwable {
        throw t;
    }
}
