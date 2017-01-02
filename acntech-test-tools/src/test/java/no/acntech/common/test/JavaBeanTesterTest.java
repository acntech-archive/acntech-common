package no.acntech.common.test;

import java.beans.IntrospectionException;

import org.junit.Test;

import no.acntech.common.test.objects.ObjectType;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JavaBeanTesterTest {

    @Test
    public void testUsingNullClass() throws IntrospectionException {
        try {
            JavaBeanTester.testClass(null);

            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue("Exception is of wrong type", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testUsingNullClasses() throws IntrospectionException {
        try {
            JavaBeanTester.testClasses((Class<?>[]) null);

            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue("Exception is of wrong type", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testUsingTestBean() throws IntrospectionException {
        JavaBeanTester.testClasses(ObjectType.class);
    }

    @Test
    public void testUsingTestBeanWithSkipParams() throws IntrospectionException {
        JavaBeanTester.testClass(ObjectType.class, "str", "int", "obj");
    }
}
