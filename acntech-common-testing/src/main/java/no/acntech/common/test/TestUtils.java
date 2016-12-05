package no.acntech.common.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class TestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    private TestUtils() {
    }

    public static void setPrivateField(Object target, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        if (target == null) {
            throw new IllegalArgumentException("Target object is null");
        }

        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(Boolean.TRUE);
        field.set(target, value);
    }

    public static void invokePrivateMethod(Object target, String methodName, Object... args) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (target == null) {
            throw new IllegalArgumentException("Target object is null");
        }

        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(Boolean.TRUE);
        method.invoke(target, args);
    }

    static <T> T createBean(final Class<T> clazz, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        final Constructor<?> constructor = getConstructor(clazz);
        return createBean(constructor, args);
    }

    static <T> T createBean(final Constructor<?> constructor, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (constructor == null) {
            throw new IllegalArgumentException("Constructor is null");
        }

        if (args != null) {
            return (T) constructor.newInstance(args);
        }

        int minParams = constructor.getParameterTypes().length;
        if (minParams == 0) {
            return (T) constructor.newInstance();
        } else {
            return (T) constructor.newInstance(new Object[minParams]);
        }
    }

    static Constructor<?>[] getConstructors(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Input class is null");
        }

        Constructor<?>[] constructors = clazz.getConstructors();
        return constructors.length > 0 ? constructors : clazz.getDeclaredConstructors();
    }

    static Constructor<?> getConstructor(final Class<?> clazz) {
        Constructor<?>[] constructors = getConstructors(clazz);

        if (constructors == null || constructors.length == 0) {
            return null;
        }

        int minParams = Integer.MAX_VALUE;
        int index = 0;

        for (int i = 0; i < constructors.length; i++) {
            int params = constructors[i].getParameterTypes().length;
            if (params < minParams) {
                minParams = params;
                index = i;
            }
        }

        return constructors[index];
    }

    static <T> List<GetterSetter> findGettersAndSetters(final Class<T> clazz, final String... skipThese) throws IntrospectionException {
        Set<String> skipFields = new HashSet<>(Arrays.asList(skipThese));

        List<GetterSetter> gettersAndSetters = new ArrayList<>();

        final PropertyDescriptor[] descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();

        for (PropertyDescriptor descriptor : descriptors) {

            if (skipFields.contains(descriptor.getName())) {
                LOGGER.info("Skipping field {} as ordered", descriptor.getName());
                continue;
            }

            findBooleanGetters(clazz, descriptor);

            final Method getter = descriptor.getReadMethod();
            final Method setter = descriptor.getWriteMethod();

            if (getter != null && setter != null) {

                final Class<?> returnType = getter.getReturnType();
                final Class<?>[] params = setter.getParameterTypes();

                if (params.length == 1 && params[0] == returnType) {
                    gettersAndSetters.add(new GetterSetter(descriptor, getter, setter));
                } else {
                    LOGGER.debug("Getter and setter for field {} has non matching type", descriptor.getName());
                }
            } else {
                LOGGER.debug("Getter or setter missing for field {}", descriptor.getName());
            }
        }

        return gettersAndSetters;
    }

    private static <T> void findBooleanGetters(Class<T> clazz, PropertyDescriptor descriptor) throws IntrospectionException {
        if (descriptor.getReadMethod() == null && descriptor.getPropertyType() == Boolean.class) {
            LOGGER.debug("Getter for field {} is boolean", descriptor.getName());
            try {
                PropertyDescriptor pd = new PropertyDescriptor(descriptor.getName(), clazz);
                descriptor.setReadMethod(pd.getReadMethod());
            } catch (IntrospectionException e) {
                LOGGER.debug("Error while finding boolean getter", e);
            }
        } else {
            LOGGER.trace("Getter for field {} is non boolean", descriptor.getName());
        }
    }
}
