package no.acntech.common.test;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import no.acntech.common.test.testsubject.DummyObjectWithAdvancedTypes;
import no.acntech.common.test.testsubject.DummyObjectWithNoDefaultConstructor;
import no.acntech.common.test.testsubject.DummyObjectWithPrimitives;

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
    public void testClassesUsingTestBeanWithPrimitives() throws Exception {
        JavaBeanTester.testClasses(DummyObjectWithPrimitives.class);
    }

    @Test
    public void testClassesUsingTestBeanWithAdvancedTypes() throws Exception {
        JavaBeanTester.testClasses(DummyObjectWithAdvancedTypes.class);
    }

    @Test
    public void testClassUsingTestBeanWithPrimitivesSkippingParams() throws Exception {
        JavaBeanTester.testClass(DummyObjectWithPrimitives.class, "str", "int", "obj");
    }

    @Test
    public void testClassesPackageIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        JavaBeanTester.testClasses((Package) null);
    }

    @Test
    public void testClassesInPackage() throws Exception {
        JavaBeanTester.testClasses(DummyObjectWithPrimitives.class.getPackage());
    }

    @Test
    public void testClassesInPackageCriteriaIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        JavaBeanTester.testClasses(DummyObjectWithPrimitives.class.getPackage(), null);
    }

    @Test
    public void testClassesInPackageWithCriteria() throws Exception {
        JavaBeanTester.testClasses(DummyObjectWithPrimitives.class.getPackage(), ClassCriteria.createDefault().build());
    }

    @Test
    public void testClassesUsingTestBeanWithNoDefaultConstructor() throws Exception {
        JavaBeanTester.testClasses(DummyObjectWithNoDefaultConstructor.class);
    }
}
