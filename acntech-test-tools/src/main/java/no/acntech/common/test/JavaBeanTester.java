package no.acntech.common.test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class JavaBeanTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaBeanTester.class);

    private JavaBeanTester() {
    }

    /**
     * Too generic method name.
     *
     * @deprecated use {@link #testClasses(Class[])} ()} instead.
     */
    @Deprecated
    public static void test(final Class<?>... classes) throws IntrospectionException {
        testClasses(classes);
    }

    /**
     * Too generic method name.
     *
     * @deprecated use {@link #testClass(Class)} ()} instead.
     */
    @Deprecated
    public static void test(final Class<?> clazz) throws IntrospectionException {
        testClass(clazz);
    }

    /**
     * Too generic method name.
     *
     * @deprecated use {@link #testClass(Class, String...)} ()} instead.
     */
    @Deprecated
    public static void test(final Class<?> clazz, final String... skipTheseFields) throws IntrospectionException {
        testClass(clazz, skipTheseFields);
    }

    /**
     * Test getters and setters for given classes.
     *
     * @param classes Classes to test.
     * @throws IntrospectionException   If an exception occurs during introspection.
     * @throws IllegalArgumentException If passed class array is null.
     */
    public static void testClasses(final Class<?>... classes) throws IntrospectionException {
        if (classes == null) {
            throw new IllegalArgumentException("Input classes is null");
        }

        for (Class<?> clazz : classes) {
            testClass(clazz);
        }
    }

    /**
     * Test getters and setters for given class.
     *
     * @param clazz Class to test.
     * @throws IntrospectionException   If an exception occurs during introspection.
     * @throws IllegalArgumentException If passed class array is null.
     */
    public static void testClass(final Class<?> clazz) throws IntrospectionException {
        if (clazz == null) {
            throw new IllegalArgumentException("Input class is null");
        }

        testClass(clazz, new String[0]);
    }

    /**
     * Test getters and setters for given class.
     *
     * @param clazz           Class to test.
     * @param skipTheseFields Names of fields that should not be tested.
     * @throws IntrospectionException   If an exception occurs during introspection.
     * @throws IllegalArgumentException If passed class array is null.
     */
    public static void testClass(final Class<?> clazz, final String... skipTheseFields) throws IntrospectionException {
        if (clazz == null) {
            throw new IllegalArgumentException("Input class is null");
        }

        if (skipTheseFields == null) {
            throw new IllegalArgumentException("Skip fields array is null");
        }

        String[] skipTheseFieldsIncludingClass = Arrays.copyOf(skipTheseFields, skipTheseFields.length + 1);
        skipTheseFieldsIncludingClass[skipTheseFields.length] = "class";

        List<GetterSetter> gettersAndSetters = TestReflectionUtils.findGettersAndSetters(clazz, skipTheseFieldsIncludingClass);

        for (GetterSetter getterSetter : gettersAndSetters) {
            testSetterAndGetter(clazz, getterSetter);
        }

        List<Getter> getters = TestReflectionUtils.findGetters(clazz, skipTheseFieldsIncludingClass);

        for (Getter getter : getters) {
            testConstructorAndGetter(clazz, getter);
        }
    }

    /**
     * Test getters and setters for all classes found in package.
     *
     * @param pkg Package to search for classes from.
     * @throws IOException              If reading using classloader fails.
     * @throws ClassNotFoundException   If creating class for a class name fails.
     * @throws IntrospectionException   If an exception occurs during introspection.
     * @throws IllegalArgumentException If passed package is null.
     */
    public static void testClasses(final Package pkg) throws IOException, ClassNotFoundException, IntrospectionException {
        testClasses(pkg, ClassCriteria.createDefault().build());
    }

    /**
     * Test getters and setters for all classes found in package depending on search criteria.
     *
     * @param pkg           Package to search for classes from.
     * @param classCriteria Package search criteria for classes.
     * @throws IOException              If reading using classloader fails.
     * @throws ClassNotFoundException   If creating class for a class name fails.
     * @throws IntrospectionException   If an exception occurs during introspection.
     * @throws IllegalArgumentException If passed package is null.
     */
    public static void testClasses(final Package pkg, ClassCriteria classCriteria) throws IOException, ClassNotFoundException, IntrospectionException {
        Class<?>[] classes = TestReflectionUtils.findClasses(pkg, classCriteria);
        testClasses(classes);
    }

    private static void testSetterAndGetter(final Class<?> clazz, GetterSetter getterSetter) {
        final PropertyDescriptor descriptor = getterSetter.getDescriptor();
        final Method getterMethod = getterSetter.getGetter();
        final Method setterMethod = getterSetter.getSetter();

        try {
            final Object expectedType = TestTypeFactory.createType(getterMethod.getReturnType());

            final Object bean = TestReflectionUtils.createBean(clazz);

            setterMethod.invoke(bean, expectedType);

            final Object actualType = getterMethod.invoke(bean);

            assertThat("Failed when testing field " + descriptor.getName(), expectedType, is(actualType));

        } catch (Exception e) {
            String error = "An exception was thrown during test of field " + descriptor.getName() + " on bean of type " + clazz.getName();
            LOGGER.error(error, e);
            fail(String.format("%s: %s", error, e.toString()));
        }
    }

    private static void testConstructorAndGetter(final Class<?> clazz, Getter getter) {
        final PropertyDescriptor descriptor = getter.getDescriptor();
        final Method getterMethod = getter.getGetter();

        try {
            final Object expectedType = TestTypeFactory.createType(getterMethod.getReturnType());

            Constructor<?>[] constructors = TestReflectionUtils.findConstructorsWithParamMatch(clazz, getterMethod.getReturnType());

            for (final Constructor<?> constructor : constructors) {
                testConstructorAndGetter(constructor, getter, expectedType);
            }
        } catch (Exception e) {
            LOGGER.trace("An exception was thrown during test of field " + descriptor.getName() + " on bean of type " + clazz.getName(), e);
        }
    }

    private static void testConstructorAndGetter(final Constructor<?> constructor, Getter getter, final Object expectedType) {
        final PropertyDescriptor descriptor = getter.getDescriptor();
        final Method getterMethod = getter.getGetter();

        Class<?>[] params = constructor.getParameterTypes();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            if (params[i].isAssignableFrom(getterMethod.getReturnType())) {
                args[i] = expectedType;
            } else {
                args[i] = null;
            }
        }

        try {
            final Object bean = TestReflectionUtils.createBean(constructor, args);

            final Object actualType = getterMethod.invoke(bean);

            if (actualType != expectedType) {
                LOGGER.warn("Constructor did not set same class field as used for getter");
            }
        } catch (Exception e) {
            LOGGER.trace("An exception was thrown during test of field " + descriptor.getName() + " on bean of type " + constructor.getDeclaringClass().getName(), e);
        }
    }
}
