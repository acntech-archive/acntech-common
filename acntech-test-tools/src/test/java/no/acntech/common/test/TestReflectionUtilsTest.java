package no.acntech.common.test;

import java.lang.reflect.Constructor;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import no.acntech.common.test.testsubject.DummyObjectWithNoDefaultConstructor;
import no.acntech.common.test.testsubject.DummyObjectWithPrimitives;
import no.acntech.common.test.testsubject.subpackage.DummySubObject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class TestReflectionUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSetInternalFieldTargetIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        TestReflectionUtils.setInternalField(null, "whatever", "whatever");
    }

    @Test
    public void testSetInternalFieldNoSuchField() throws Exception {
        thrown.expect(NoSuchFieldException.class);
        DummyObjectWithPrimitives subject = new DummyObjectWithPrimitives();

        TestReflectionUtils.setInternalField(subject, "whatever", "whatever");
    }

    @Test
    public void testSetInternalField() throws Exception {
        DummyObjectWithPrimitives subject = new DummyObjectWithPrimitives();

        TestReflectionUtils.setInternalField(subject, "str", "1337");

        assertThat("Field value is different in getter", subject.getStr(), is("1337"));
    }

    @Test
    public void testInvokePrivateMethodTargetIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        TestReflectionUtils.invokePrivateMethod(null, "whatever");
    }

    @Test
    public void testInvokePrivateMethodNoSuchMethod() throws Exception {
        thrown.expect(NoSuchMethodException.class);
        DummyObjectWithPrimitives subject = new DummyObjectWithPrimitives();

        TestReflectionUtils.invokePrivateMethod(subject, "whatever");
    }

    @Test
    public void testInvokePrivateMethodSetter() throws Exception {
        DummyObjectWithPrimitives subject = new DummyObjectWithPrimitives();

        assertThat("Value is already set", subject.getStr(), nullValue());

        Object returnObject = TestReflectionUtils.invokePrivateMethod(subject, "setStr", "1337");

        assertThat("Return object is not null", returnObject, nullValue());
        assertThat("Value not set by setter", subject.getStr(), is("1337"));
    }

    @Test
    public void testInvokePrivateMethodGetter() throws Exception {
        DummyObjectWithPrimitives subject = new DummyObjectWithPrimitives();
        subject.setStr("1337");

        Object returnObject = TestReflectionUtils.invokePrivateMethod(subject, "getStr");

        assertThat("Return object is null", returnObject, notNullValue());
        assertThat("Value not correct", returnObject.toString(), is("1337"));
    }

    @Test
    public void testCreateBeanTargetIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        TestReflectionUtils.createBean((Class<Object>) null);
    }

    @Test
    public void testCreateBeanConstructorIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        TestReflectionUtils.createBean((Constructor<Object>) null);
    }

    @Test
    public void testCreateBeanWithDefaultConstructor() throws Exception {
        DummyObjectWithPrimitives subject = TestReflectionUtils.createBean(DummyObjectWithPrimitives.class);

        assertThat("Object is null", subject, notNullValue());
    }

    @Test
    public void testCreateBeanWithNoDefaultConstructorShouldThrowNoSuchConstructorException() throws Exception {
        thrown.expect(NoSuchConstructorException.class);

        TestReflectionUtils.createBean(DummyObjectWithNoDefaultConstructor.class);
    }

    @Test
    public void testCreateBeanWithNoDefaultConstructor() throws Exception {
        DummyObjectWithNoDefaultConstructor object = TestReflectionUtils.createBean(DummyObjectWithNoDefaultConstructor.class, "1337");

        assertThat("Object is null", object, notNullValue());
        assertThat("Value not correct", object.getStr(), is("1337"));
    }

    @Test
    public void testFindClassesPackageIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        TestReflectionUtils.findClasses((Package) null, ClassCriteria.createDefault().build());
    }

    @Test
    public void testFindClassesPackageNameIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        TestReflectionUtils.findClasses((String) null, ClassCriteria.createDefault().build());
    }

    @Test
    public void testFindClassesClassCriteriaIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        TestReflectionUtils.findClasses(DummyObjectWithPrimitives.class.getPackage(), null);
    }

    @Test
    public void testFindClassesInPackageDefaultSearchCriteria() throws Exception {
        Class<?>[] classes = TestReflectionUtils.findClasses(DummyObjectWithPrimitives.class.getPackage(), ClassCriteria.createDefault().build());

        assertThat("Package classes are null", classes, notNullValue());
        assertThat("Wrong number og classes found in package", classes.length, is(9));
    }

    @Test
    public void testFindClassesInPackageExcludeInterfaces() throws Exception {
        Class<?>[] classes = TestReflectionUtils.findClasses(DummyObjectWithPrimitives.class.getPackage(), ClassCriteria.createDefault().doExcludeInterfaces().build());

        assertThat("Package classes are null", classes, notNullValue());
        assertThat("Wrong number og classes found in package", classes.length, is(7)); // Annotations are also interfaces
    }

    @Test
    public void testFindClassesInPackageExcludeEnums() throws Exception {
        Class<?>[] classes = TestReflectionUtils.findClasses(DummyObjectWithPrimitives.class.getPackage(), ClassCriteria.createDefault().doExcludeEnums().build());

        assertThat("Package classes are null", classes, notNullValue());
        assertThat("Wrong number og classes found in package", classes.length, is(8));
    }

    @Test
    public void testFindClassesInPackageExcludeAnnotations() throws Exception {
        Class<?>[] classes = TestReflectionUtils.findClasses(DummyObjectWithPrimitives.class.getPackage(), ClassCriteria.createDefault().doExcludeAnnotations().build());

        assertThat("Package classes are null", classes, notNullValue());
        assertThat("Wrong number og classes found in package", classes.length, is(8));
    }

    @Test
    public void testFindClassesInPackageExcludeMemberClasses() throws Exception {
        Class<?>[] classes = TestReflectionUtils.findClasses(DummyObjectWithPrimitives.class.getPackage(), ClassCriteria.createDefault().doExcludeMemberClasses().build());

        assertThat("Package classes are null", classes, notNullValue());
        assertThat("Wrong number og classes found in package", classes.length, is(8));
    }

    @Test
    public void testFindClassesInPackageExcludeAll() throws Exception {
        Class<?>[] classes = TestReflectionUtils.findClasses(DummyObjectWithPrimitives.class.getPackage(), ClassCriteria.createDefault().doExcludeAll().build());

        assertThat("Package classes are null", classes, notNullValue());
        assertThat("Wrong number og classes found in package", classes.length, is(5));
    }

    @Test
    public void testFindClassesInPackageRecursiveSearchCriteria() throws Exception {
        Class<?>[] classes = TestReflectionUtils.findClasses(DummyObjectWithPrimitives.class.getPackage(), ClassCriteria.createRecursive().build());

        assertThat("Package classes are null", classes, notNullValue());
        assertThat("Wrong number og classes found in package", classes.length, is(11));
    }

    @Test
    public void testFindClassesInPackageExcludeMavenTestClasses() throws Exception {
        Class<?>[] classes = TestReflectionUtils.findClasses(DummyObjectWithPrimitives.class.getPackage(), ClassCriteria.createDefault().doExcludePaths("test-classes").build());

        assertThat("Package classes are null", classes, notNullValue());
        assertThat("Array of classes is not empty", classes.length, is(0));
    }

    @Test
    public void testFindGettersAndSettersClassIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);

        TestReflectionUtils.findGettersAndSetters(null);
    }

    @Test
    public void testFindGettersAndSettersClassWithoutFields() throws Exception {
        List<GetterSetter> gettersAndSetters = TestReflectionUtils.findGettersAndSetters(DummySubObject.class);

        assertThat("List is null", gettersAndSetters, notNullValue());
        assertThat("List of getters and setters is not empty", gettersAndSetters, is(empty()));
    }

    @Test
    public void testFindGettersAndSetters() throws Exception {
        List<GetterSetter> gettersAndSetters = TestReflectionUtils.findGettersAndSetters(DummyObjectWithPrimitives.class);

        assertThat("List is null", gettersAndSetters, notNullValue());
        assertThat("List of getters and setters is not empty", gettersAndSetters, hasSize(16));
    }

    @Test
    public void testFindGettersAndSettersSkippingSome() throws Exception {
        List<GetterSetter> gettersAndSetters = TestReflectionUtils.findGettersAndSetters(DummyObjectWithPrimitives.class, "bool2", "chr");

        assertThat("List is null", gettersAndSetters, notNullValue());
        assertThat("List of getters and setters is not empty", gettersAndSetters, hasSize(14));
    }

    @Test
    public void testFindGetters() throws Exception {
        List<Getter> getters = TestReflectionUtils.findGetters(DummyObjectWithNoDefaultConstructor.class, "class");

        assertThat("List is null", getters, notNullValue());
        assertThat("List of getters and setters is not empty", getters, hasSize(1));
    }
}