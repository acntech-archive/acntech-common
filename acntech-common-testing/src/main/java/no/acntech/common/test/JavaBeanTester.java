package no.acntech.common.test;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
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

    public static <T> void test(final Class<T>... classes) throws IntrospectionException {
        if (classes == null) {
            throw new IllegalArgumentException("Input classes is null");
        }

        for (Class<T> clazz : classes) {
            test(clazz);
        }
    }

    public static <T> void test(final Class<T> clazz) throws IntrospectionException {
        if (clazz == null) {
            throw new IllegalArgumentException("Input class is null");
        }

        test(clazz, new String[0]);
    }

    public static <T> void test(final Class<T> clazz, final String... skipTheseFields) throws IntrospectionException {

        List<GetterSetter> gettersAndSetters = TestUtils.findGettersAndSetters(clazz, skipTheseFields);

        for (GetterSetter getterSetter : gettersAndSetters) {

            final PropertyDescriptor descriptor = getterSetter.getDescriptor();
            final Method getter = getterSetter.getGetter();
            final Method setter = getterSetter.getSetter();

            try {
                final Object expectedType = createType(getter.getReturnType());

                T bean = TestUtils.createBean(clazz);

                setter.invoke(bean, expectedType);

                final Object actualType = getter.invoke(bean);

                assertEquals(String.format("Failed when testing types %s", descriptor.getName()), expectedType, actualType);

            } catch (Exception e) {
                String error = String.format("An exception was thrown during bean no.acntech.common.no.acntech.common.test %s", descriptor.getName());
                LOGGER.error(error, e);
                fail(String.format("%s: %s", error, e.toString()));
            }
        }
    }

    private static Object createType(Class<?> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, DatatypeConfigurationException {

        Object object = createBasicType(clazz);
        if (object != null) {
            return object;
        }

        object = createObjectType(clazz);
        if (object != null) {
            return object;
        }

        object = createMockType(clazz);
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

    private static Object createObjectType(Class<?> clazz) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Constructor<?>[] constructors = clazz.getConstructors();

        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                return constructor.newInstance();
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
}
