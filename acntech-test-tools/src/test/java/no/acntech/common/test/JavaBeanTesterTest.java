package no.acntech.common.test;

import org.junit.Test;

import java.beans.IntrospectionException;

public class JavaBeanTesterTest {

    @Test
    public void testUsingTestBean() throws IntrospectionException {
        JavaBeanTester.test(TestBean.class);
    }

    @Test
    public void testUsingTestBeanWithSkipParams() throws IntrospectionException {
        JavaBeanTester.test(TestBean.class, "str", "int", "obj");
    }
}
