package no.acntech.common.test;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public final class JavaBeanTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaBeanTester.class);

    private JavaBeanTester() {
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

        List<GetterSetter> gettersAndSetters = TestReflectionUtils.findGettersAndSetters(clazz, skipTheseFields);

        for (GetterSetter getterSetter : gettersAndSetters) {

            final PropertyDescriptor descriptor = getterSetter.getDescriptor();
            final Method getter = getterSetter.getGetter();
            final Method setter = getterSetter.getSetter();

            try {
                final Object expectedType = createType(getter.getReturnType());

                Object bean = TestReflectionUtils.createBean(clazz);

                setter.invoke(bean, expectedType);

                final Object actualType = getter.invoke(bean);

                assertEquals(String.format("Failed when testing types %s", descriptor.getName()), expectedType, actualType);

            } catch (Exception e) {
                String error = String.format("An exception was thrown during bean test %s", descriptor.getName());
                LOGGER.error(error, e);
                fail(String.format("%s: %s", error, e.toString()));
            }
        }
    }

    /**
     * Test getters and setters for all classes found in package and subpackages.
     *
     * @param pkg Package to search for classes from.
     * @throws IOException              If reading using classloader fails.
     * @throws ClassNotFoundException   If creating class for a class name fails.
     * @throws IntrospectionException   If an exception occurs during introspection.
     * @throws IllegalArgumentException If passed package is null.
     */
    public static void testClasses(Package pkg) throws IOException, ClassNotFoundException, IntrospectionException {
        Class<?>[] classes = TestReflectionUtils.findClasses(pkg);
        testClasses(classes);
    }

    private static Object createType(Class<?> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, DatatypeConfigurationException {

        Object object = createBasicType(clazz);
        if (object != null) {
            return object;
        }

        object = createMockType(clazz);
        if (object != null) {
            return object;
        }

        object = createObjectType(clazz);
        if (object != null) {
            return object;
        }

        fail(String.format("Could not create bean object of class %s, please extend the %s class to prevent this.", clazz.getName(), JavaBeanTester.class.getName()));
        return null;
    }

    private static <T> Object createBasicType(Class<T> clazz) {
        List<BasicType> basicTypes = BasicType.getBasicTypes();
        for (BasicType basicType : basicTypes) {
            if (basicType.isType(clazz)) {
                return basicType.getType(clazz);
            }
        }
        return null;
    }

    private static Object createMockType(Class<?> clazz) {
        if (!Modifier.isFinal(clazz.getModifiers())) {
            return Mockito.mock(clazz);
        } else {
            return null;
        }
    }

    private static Object createObjectType(Class<?> clazz) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return TestReflectionUtils.createBean(clazz);
    }
}
