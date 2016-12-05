package no.acntech.common.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public final class ExceptionTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionTester.class);

    private ExceptionTester() {
    }

    public static void test(Class<? extends Throwable>... throwables) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if (throwables == null) {
            throw new IllegalArgumentException("Input is null");
        }

        for (Class<? extends Throwable> throwable : throwables) {
            Throwable t = TestUtils.createBean(throwable);
            try {
                throw t;
            } catch (Throwable e) {
                LOGGER.info("Throwable tested", e);
            }
        }
    }
}
