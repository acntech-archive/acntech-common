package no.acntech.common.test;

import org.junit.Test;

public class TestExceptionTest {

    @Test
    public void testExceptions() throws Exception {
        ExceptionTester.test(TestException.class);
    }
}
