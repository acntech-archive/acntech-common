package no.acntech.common.test;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestExceptionTest {

    @Test
    public void testExceptionUsingNull() throws Exception {
        try {
            ExceptionTester.testException(null);

            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue("Exception is of wrong type", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testExceptionsUsingNull() throws Exception {
        try {
            ExceptionTester.testExceptions(null);

            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue("Exception is of wrong type", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testExceptions() throws Exception {
        ExceptionTester.testExceptions(TestException.class);
    }
}
