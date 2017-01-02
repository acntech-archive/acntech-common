package no.acntech.common.test;

import no.acntech.common.test.objects.ObjectType;
import org.junit.Test;

import java.beans.IntrospectionException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JavaBeanTesterTest {

    @Test
    public void testClassUsingNull() throws IntrospectionException {
        try {
            JavaBeanTester.testClass(null);

            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue("Exception is of wrong type", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testClassesUsingNull() throws IntrospectionException {
        try {
            JavaBeanTester.testClasses((Class<?>[]) null);

            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue("Exception is of wrong type", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testClassesUsingTestBean() throws IntrospectionException {
        JavaBeanTester.testClasses(ObjectType.class);
    }

    @Test
    public void testClassUsingTestBeanWithSkipParams() throws IntrospectionException {
        JavaBeanTester.testClass(ObjectType.class, "str", "int", "obj");
    }
}
