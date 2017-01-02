package no.acntech.common.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TestReflectionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestReflectionUtils.class);
    private static final char PKG_SEPARATOR = '.';
    private static final char DIR_SEPARATOR = '/';
    private static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * Set the value of the field of an object.
     *
     * @param target    The target object to set the field value of.
     * @param fieldName The name of the field to set the value of.
     * @param value     The value to set into the object field.
     * @throws IllegalArgumentException If target object is null.
     * @throws IllegalAccessException   If unable to set the field value of the target object.
     * @throws NoSuchFieldException     If no field found for fieldName.
     */
    public static void setInternalField(final Object target, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        if (target == null) {
            throw new IllegalArgumentException("Target object is null");
        }

        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(Boolean.TRUE);
        field.set(target, value);
    }

    /**
     * @param target     The target object to invoke method of.
     * @param methodName The name of the method to invoke.
     * @param args       The arguments of the method to be invoked.
     * @return
     * @throws IllegalArgumentException  If target object is null.
     * @throws IllegalAccessException    If access to method is illegal.
     * @throws NoSuchMethodException     If no method can be found for name.
     * @throws InvocationTargetException If the method throws an exception.
     */
    public static Object invokePrivateMethod(final Object target, String methodName,
                                             Object... args) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (target == null) {
            throw new IllegalArgumentException("Target object is null");
        }

        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(Boolean.TRUE);
        return method.invoke(target, args);
    }

    static <T> T createBean(final Class<T> clazz, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        final Constructor<T> constructor = findDefaultConstructor(clazz);
        return createBean(constructor, args);
    }

    static <T> T createBean(final Constructor<T> constructor, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (constructor == null) {
            throw new IllegalArgumentException("Constructor is null");
        }

        if (args != null) {
            return constructor.newInstance(args);
        }

        int minParams = constructor.getParameterTypes().length;
        if (minParams == 0) {
            return constructor.newInstance();
        } else {
            return constructor.newInstance(new Object[minParams]);
        }
    }

    static <T> Constructor<T>[] getConstructors(final Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Input class is null");
        }

        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length > 0) {
            return (Constructor<T>[]) constructors;
        } else {
            return (Constructor<T>[]) clazz.getDeclaredConstructors();
        }
    }

    static <T> Constructor<T> findDefaultConstructor(final Class<T> clazz) {
        Constructor<T>[] constructors = getConstructors(clazz);

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

    static <T> Constructor<T> findConstructorWithParameters(final Class<T> clazz, final Class<?>... args) {
        Constructor<T>[] constructors = getConstructors(clazz);

        if (constructors != null) {
            for (Constructor<T> constructor : constructors) {
                Class<?>[] params = constructor.getParameterTypes();
                if (isParametersMatch(args, params)) {
                    return constructor;
                }
            }
        }

        return null;
    }

    /**
     * Find classes within a package and subpackages.
     *
     * @param pkg     Package to search for classes from.
     * @param recurse Search for classes recursively.
     * @return Classes found within a package and subpackages.
     * @throws IOException            If reading using classloader fails.
     * @throws ClassNotFoundException If creating class for a class name fails.
     */
    static Class<?>[] findClasses(final Package pkg, boolean recurse) throws IOException, ClassNotFoundException {
        if (pkg == null) {
            throw new IllegalArgumentException("Package is null");
        }

        return findClasses(pkg.getName(), recurse);
    }

    /**
     * Find classes within a package and subpackages.
     *
     * @param packageName Package name to search for classes from.
     * @param recurse     Search for classes recursively.
     * @return Classes found within a package and subpackages.
     * @throws IOException            If reading using classloader fails.
     * @throws ClassNotFoundException If creating class for a class name fails.
     */
    static Class<?>[] findClasses(String packageName, boolean recurse) throws IOException, ClassNotFoundException {
        if (packageName == null) {
            throw new IllegalArgumentException("Package name is null");
        }

        LOGGER.info("Searching for classes in package {}{}", packageName, recurse ? " recursively" : "");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;

        String path = packageName.replace(PKG_SEPARATOR, DIR_SEPARATOR);

        LOGGER.trace("Converted package {} to path {}", packageName, path);

        Enumeration<URL> resources = classLoader.getResources(path);

        List<File> directories = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            directories.add(new File(resource.getFile()));
        }

        List<Class<?>> classes = new ArrayList<>();
        for (File directory : directories) {
            classes.addAll(findClasses(directory, packageName, recurse));
        }

        LOGGER.info("Found {} classes in package {}{}", classes.size(), packageName, recurse ? " with child packages" : "");

        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class<?>> findClasses(File directory, String packageName, boolean recurse) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        if (!directory.exists()) {
            LOGGER.debug("Directory {} does not exist, so skipping", directory.getAbsolutePath());
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            LOGGER.debug("No children found in directory {}, so skipping", directory.getAbsolutePath());
            return classes;
        }

        LOGGER.debug("Searching for classes in package {} in directory {}", packageName, directory.getAbsolutePath());

        for (File file : files) {
            if (file.isDirectory() && recurse) {
                assert !file.getName().contains(String.valueOf(PKG_SEPARATOR));
                String subPackageName = packageName + String.valueOf(PKG_SEPARATOR) + file.getName();
                classes.addAll(findClasses(file, subPackageName, recurse));
            } else if (file.isFile() && file.getName().endsWith(CLASS_FILE_SUFFIX)) {
                String className = packageName + String.valueOf(PKG_SEPARATOR) + file.getName().replace(CLASS_FILE_SUFFIX, "");
                LOGGER.trace("Found class {} in directory {}", className, directory.getAbsolutePath());
                classes.add(Class.forName(className));
            }
        }
        return classes;
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

    private static boolean isParametersMatch(final Class<?>[] params1, final Class<?>[] params2) {
        if (params1 == null || params2 == null || params1.length != params2.length) {
            return Boolean.FALSE;
        }
        boolean allMatch = Boolean.TRUE;
        for (int i = 0; i < params1.length; i++) {
            allMatch = allMatch && params1[i].isAssignableFrom(params2[i]);
        }
        return allMatch;
    }
}
