package no.acntech.common.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public final class ExceptionTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionTester.class);

    private ExceptionTester() {
    }

    /**
     * Test exceptions.
     *
     * @param throwables Exceptions to test.
     * @throws IllegalAccessException    If access control denied access to constructor of exception.
     * @throws InstantiationException    If constructor of exception is from an abstract class.
     * @throws InvocationTargetException If constructor of exception throws exception.
     */
    @SafeVarargs
    public static void test(Class<? extends Throwable>... throwables) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (throwables == null) {
            throw new IllegalArgumentException("Input is null");
        }

        for (Class<? extends Throwable> throwable : throwables) {
            Throwable t = TestReflectionUtils.createBean(throwable);
            try {
                throw t;
            } catch (Throwable e) {
                LOGGER.info("Throwable tested", e);
            }
        }
    }
}
