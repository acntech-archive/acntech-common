package no.acntech.common.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class JavaBeanTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaBeanTester.class);
    private static final String GENERAL_EXCEPTION_MESSAGE_FORMAT = "An exception was thrown during test of field %s on bean of type %s";
    private static final String OBJECT_INSTANTIATION_EXCEPTION_MESSAGE_FORMAT = "Could not create object for field %s of type %s on bean of type %s.\n" +
            "Add custom types by using no.acntech.common.testTestTypeFactory.addBasicType(BasicType basicType)";

    private JavaBeanTester() {
    }

    /**
     * Method name is too generic.
     *
     * @deprecated use {@link #testClasses(Class[])} ()} instead.
     */
    @Deprecated
    public static void test(final Class<?>... classes) throws IntrospectionException {
        testClasses(classes);
    }

    /**
     * Method name is too generic.
     *
     * @deprecated use {@link #testClass(Class)} ()} instead.
     */
    @Deprecated
    public static void test(final Class<?> clazz) throws IntrospectionException {
        testClass(clazz);
    }

    /**
     * Method name is too generic.
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

        testClass(clazz, FieldCriteria.createDefault().doExcludeFields(skipTheseFields).build());
    }

    /**
     * Test getters and setters for given class.
     *
     * @param clazz         Class to test.
     * @param fieldCriteria Search criteria for the fields to be tested.
     * @throws IntrospectionException   If an exception occurs during introspection.
     * @throws IllegalArgumentException If passed class array is null.
     */
    public static void testClass(final Class<?> clazz, final FieldCriteria fieldCriteria) throws IntrospectionException {
        if (clazz == null) {
            throw new IllegalArgumentException("Input class is null");
        }

        if (fieldCriteria == null) {
            throw new IllegalArgumentException("Field criteria is null");
        }

        List<GetterSetter> gettersAndSetters = TestReflectionUtils.findGettersAndSetters(clazz, fieldCriteria);

        for (GetterSetter getterSetter : gettersAndSetters) {
            testSetterAndGetter(clazz, getterSetter);
        }

        List<Getter> getters = TestReflectionUtils.findGetters(clazz, fieldCriteria);

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
     * @param classCriteria Search criteria for the classes to be tested.
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
        final Class<?> returnType = getterMethod.getReturnType();

        try {
            final Object expectedType = TestTypeFactory.createType(returnType);

            final Object bean = TestReflectionUtils.createBean(clazz);

            setterMethod.invoke(bean, expectedType);

            final Object actualType = getterMethod.invoke(bean);

            assertThat("Failed when testing field " + descriptor.getName(), expectedType, is(actualType));

        } catch (ObjectInstantiationException e) {
            String error = String.format(OBJECT_INSTANTIATION_EXCEPTION_MESSAGE_FORMAT, descriptor.getName(), returnType.getName(), clazz.getName());
            LOGGER.error(error, e);
            fail(error);
        } catch (Exception e) {
            String error = String.format(GENERAL_EXCEPTION_MESSAGE_FORMAT, descriptor.getName(), clazz.getName());
            LOGGER.error(error, e);
            fail(String.format("%s: %s", error, e.toString()));
        }
    }

    private static void testConstructorAndGetter(final Class<?> clazz, Getter getter) {
        final PropertyDescriptor descriptor = getter.getDescriptor();
        final Method getterMethod = getter.getGetter();
        final Class<?> returnType = getterMethod.getReturnType();

        try {
            final Object expectedType = TestTypeFactory.createType(returnType);

            Constructor<?>[] constructors = TestReflectionUtils.findConstructorsWithParamMatch(clazz, returnType);

            for (final Constructor<?> constructor : constructors) {
                testConstructorAndGetter(constructor, getter, expectedType);
            }
        } catch (ObjectInstantiationException e) {
            String error = String.format(OBJECT_INSTANTIATION_EXCEPTION_MESSAGE_FORMAT, descriptor.getName(), returnType.getName(), clazz.getName());
            LOGGER.error(error, e);
            fail(error);
        } catch (Exception e) {
            LOGGER.trace(String.format(GENERAL_EXCEPTION_MESSAGE_FORMAT, descriptor.getName(), clazz.getName()), e);
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
            LOGGER.trace(String.format(GENERAL_EXCEPTION_MESSAGE_FORMAT, descriptor.getName(), constructor.getDeclaringClass().getName()), e);
        }
    }
}
