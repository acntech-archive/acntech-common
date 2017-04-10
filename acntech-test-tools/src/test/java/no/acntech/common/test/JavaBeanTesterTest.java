package no.acntech.common.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import no.acntech.common.test.testsubject.DummyObject;

public class JavaBeanTesterTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testClassUsingNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        JavaBeanTester.testClass(null);
    }

    @Test
    public void testClassesUsingNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        JavaBeanTester.testClasses((Class<?>[]) null);
    }

    @Test
    public void testClassesUsingTestBean() throws Exception {
        JavaBeanTester.testClasses(DummyObject.class);
    }

    @Test
    public void testClassUsingTestBeanWithSkipParams() throws Exception {
        JavaBeanTester.testClass(DummyObject.class, "str", "int", "obj");
    }

    @Test
    public void testClassesPackageIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        JavaBeanTester.testClasses((Package) null);
    }

    @Test
    public void testClassesInPackage() throws Exception {
        JavaBeanTester.testClasses(DummyObject.class.getPackage());
    }
}
