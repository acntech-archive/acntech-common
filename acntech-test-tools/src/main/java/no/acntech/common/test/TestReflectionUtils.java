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
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TestReflectionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestReflectionUtils.class);
    private static final char PKG_SEPARATOR = '.';
    private static final char DIR_SEPARATOR = '/';
    private static final String CLASS_FILE_SUFFIX = ".class";

    private TestReflectionUtils() {
    }

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
    public static void setInternalField(final Object target, String fieldName, Object value) throws IllegalAccessException, NoSuchFieldException {
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
     * @return Return value of the method, unless it is a void method, which will return null;
     * @throws IllegalArgumentException  If target object is null.
     * @throws IllegalAccessException    If access to method is illegal.
     * @throws NoSuchMethodException     If no method can be found for name.
     * @throws InvocationTargetException If the method throws an exception.
     */
    public static Object invokePrivateMethod(final Object target, String methodName,
                                             Object... args) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (target == null) {
            throw new IllegalArgumentException("Target object is null");
        }

        Class<?>[] params = TestTypeFactory.getClassesForObjects(args);

        Method method = target.getClass().getDeclaredMethod(methodName, params);
        method.setAccessible(Boolean.TRUE);
        return method.invoke(target, args);
    }

    static boolean isFinalClass(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class is null");
        }

        return Modifier.isFinal(clazz.getModifiers());
    }

    static boolean isClassExists(final Class<?> clazz, final ClassLoader classLoader) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class is null");
        }

        return isClassExists(clazz.getName(), classLoader);
    }

    static boolean isClassExists(String className, final ClassLoader classLoader) {
        if (className == null) {
            throw new IllegalArgumentException("Class name is null");
        }

        if (classLoader == null) {
            throw new IllegalArgumentException("ClassLoader is null");
        }

        try {
            Class.forName(className, Boolean.FALSE, classLoader);
            return Boolean.TRUE;
        } catch (ClassNotFoundException e) {
            return Boolean.FALSE;
        }
    }

    static <T> T createBean(final Class<T> clazz, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?>[] params = TestTypeFactory.getClassesForObjects(args);

        final Constructor<T> constructor = findConstructorWithAllParamsMatch(clazz, params);

        return createBean(constructor, args);
    }

    static <T> T createBean(final Constructor<T> constructor, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (constructor == null) {
            throw new IllegalArgumentException("Constructor is null");
        }

        if (args == null || args.length == 0) {
            return constructor.newInstance();
        } else {
            return constructor.newInstance(args);
        }
    }

    static <T> Constructor<T> findConstructorWithAllParamsMatch(final Class<T> clazz, final Class<?>... wantedParams) {
        Constructor<T>[] constructors = findAllConstructors(clazz);

        if (constructors != null) {
            for (Constructor<T> constructor : constructors) {
                Class<?>[] actualParams = constructor.getParameterTypes();
                if (isAllParamsMatch(actualParams, wantedParams)) {
                    return constructor;
                }
            }
        }

        throw new NoSuchConstructorException(clazz);
    }

    @SuppressWarnings("unchecked")
    static <T> Constructor<T>[] findConstructorsWithParamMatch(final Class<T> clazz, final Class<?> wantedParam) {
        Constructor<T>[] constructors = findAllConstructors(clazz);

        List<Constructor<T>> matchedConstructors = new ArrayList<>();

        if (constructors != null) {
            for (Constructor<T> constructor : constructors) {
                Class<?>[] actualParams = constructor.getParameterTypes();
                if (isParamMatch(actualParams, wantedParam)) {
                    matchedConstructors.add(constructor);
                }
            }
        }

        if (matchedConstructors.isEmpty()) {
            throw new NoSuchConstructorException(clazz);
        } else {
            return matchedConstructors.toArray(new Constructor[matchedConstructors.size()]);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T>[] findAllConstructors(final Class<T> clazz) {
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

    /**
     * Find classes within a package depending on search criteria.
     *
     * @param pkg           Package to search for classes from.
     * @param classCriteria Package search criteria for classes.
     * @return Classes found.
     * @throws IOException            If reading using classloader fails.
     * @throws ClassNotFoundException If creating class for a class name fails.
     */
    static Class<?>[] findClasses(final Package pkg, ClassCriteria classCriteria) throws IOException, ClassNotFoundException {
        if (pkg == null) {
            throw new IllegalArgumentException("Package is null");
        }

        return findClasses(pkg.getName(), classCriteria);
    }

    /**
     * Find classes within a package depending on search criteria.
     *
     * @param packageName   Package name to search for classes from.
     * @param classCriteria Package search criteria for classes.
     * @return Classes found.
     * @throws IOException            If reading using classloader fails.
     * @throws ClassNotFoundException If creating class for a class name fails.
     */
    static Class<?>[] findClasses(String packageName, ClassCriteria classCriteria) throws IOException, ClassNotFoundException {
        if (packageName == null) {
            throw new IllegalArgumentException("Package name is null");
        }

        if (classCriteria == null) {
            throw new IllegalArgumentException("Class search criteria is null");
        }

        LOGGER.info("Searching for classes in package {}{}", packageName, classCriteria.isRecursiveSearch() ? " recursively" : "");

        Thread thread = Thread.currentThread();
        ClassLoader classLoader = thread.getContextClassLoader();
        if (classLoader == null) {
            throw new ClassloaderNullException(thread);
        }

        String path = packageName.replace(PKG_SEPARATOR, DIR_SEPARATOR);

        LOGGER.trace("Converted package {} to path {}", packageName, path);

        Enumeration<URL> resources = classLoader.getResources(path);

        List<File> files = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String pathName = URLDecoder.decode(resource.getFile(), "UTF-8");
            files.add(new File(pathName));
        }

        List<Class<?>> classes = new ArrayList<>();
        for (File file : files) {
            classes.addAll(findClasses(file, packageName, classCriteria, classes));

            if (classes.size() >= classCriteria.getMaxClassLimit()) {
                LOGGER.info("Number of classes found during package search has reached the max class limit of {}, so stopping search", classCriteria.getMaxClassLimit());
                break;
            }
        }

        LOGGER.info("Found a total of {} classes in package {}{}", classes.size(), packageName, classCriteria.isRecursiveSearch() ? " and all child packages" : "");

        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class<?>> findClasses(File directory, String packageName, ClassCriteria classCriteria, final List<Class<?>> allClasses) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        if (!directory.exists()) {
            LOGGER.debug("Directory {} does not exist, so skipping directory", directory.getAbsolutePath());
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            LOGGER.debug("No children found in directory {}, so skipping directory", directory.getAbsolutePath());
            return classes;
        }

        LOGGER.debug("Searching for classes in package {} in directory {}", packageName, directory.getAbsolutePath());

        for (File file : files) {
            if (isPathRegexMatch(classCriteria, file)) {
                continue;
            }

            if (file.isDirectory()) {
                processDirectory(file, packageName, classCriteria, classes, allClasses);
            } else if (file.isFile()) {
                processFile(directory, file, packageName, classCriteria, classes);
            } else {
                LOGGER.debug("File {} is not a directory nor a file, so skipping", file.getName());
            }

            if (allClasses.size() + classes.size() >= classCriteria.getMaxClassLimit()) {
                break;
            }
        }

        LOGGER.debug("Found {} classes in directory {}", classes.size(), directory.getAbsolutePath());

        return classes;
    }

    private static void processDirectory(File file, String packageName, ClassCriteria classCriteria, final List<Class<?>> allClasses, List<Class<?>> classes) throws ClassNotFoundException {
        if (!classCriteria.isRecursiveSearch()) {
            LOGGER.trace("Non recursive search criteria specified, so skipping directory");
        } else if (file.getName().contains(String.valueOf(PKG_SEPARATOR))) {
            LOGGER.debug("Directory {} contains character {}, so skipping directory", file.getAbsolutePath(), PKG_SEPARATOR);
        } else {
            String subPackageName = packageName + String.valueOf(PKG_SEPARATOR) + file.getName();
            classes.addAll(findClasses(file, subPackageName, classCriteria, allClasses));
        }
    }

    private static void processFile(File directory, File file, String packageName, ClassCriteria classCriteria, List<Class<?>> classes) throws ClassNotFoundException {
        if (file.getName().endsWith(CLASS_FILE_SUFFIX)) {
            String className = packageName + String.valueOf(PKG_SEPARATOR) + file.getName().replace(CLASS_FILE_SUFFIX, "");
            LOGGER.trace("Found class {} in directory {}", className, directory.getAbsolutePath());
            Class<?> clazz = Class.forName(className);
            if (clazz.isInterface() && classCriteria.isExcludeInterfaces()) {
                LOGGER.trace("Class search criteria specifies to exclude interfaces, so skipping class {}", className);
            } else if (clazz.isEnum() && classCriteria.isExcludeEnums()) {
                LOGGER.trace("Class search criteria specifies to exclude enums, so skipping class {}", className);
            } else if (clazz.isAnnotation() && classCriteria.isExcludeAnnotations()) {
                LOGGER.trace("Class search criteria specifies to exclude annotations, so skipping class {}", className);
            } else if (clazz.isMemberClass() && classCriteria.isExcludeMemberClasses()) {
                LOGGER.trace("Class search criteria specifies to exclude member classes, so skipping class {}", className);
            } else {
                classes.add(clazz);
            }
        } else {
            LOGGER.debug("File {} does not have a class file ending {}, so skipping file", file.getAbsolutePath(), CLASS_FILE_SUFFIX);
        }
    }

    static <T> List<GetterSetter> findGettersAndSetters(final Class<T> clazz) throws IntrospectionException {
        return findGettersAndSetters(clazz, FieldCriteria.createDefault().build());
    }

    static <T> List<GetterSetter> findGettersAndSetters(final Class<T> clazz, final FieldCriteria fieldCriteria) throws IntrospectionException {
        if (clazz == null) {
            throw new IllegalArgumentException("Input class is null");
        }

        List<GetterSetter> gettersAndSetters = new ArrayList<>();

        final PropertyDescriptor[] descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();

        for (PropertyDescriptor descriptor : descriptors) {

            if (fieldCriteria.getExcludeFields().contains(descriptor.getName())) {
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

    static <T> List<Getter> findGetters(final Class<T> clazz) throws IntrospectionException {
        return findGetters(clazz, FieldCriteria.createDefault().build());
    }

    static <T> List<Getter> findGetters(final Class<T> clazz, final FieldCriteria fieldCriteria) throws IntrospectionException {
        if (clazz == null) {
            throw new IllegalArgumentException("Input class is null");
        }

        List<Getter> getters = new ArrayList<>();

        final PropertyDescriptor[] descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();

        for (PropertyDescriptor descriptor : descriptors) {

            if (fieldCriteria.getExcludeFields().contains(descriptor.getName())) {
                LOGGER.info("Skipping field {} as ordered", descriptor.getName());
                continue;
            }

            findBooleanGetters(clazz, descriptor);

            final Method getter = descriptor.getReadMethod();

            if (getter != null) {
                getters.add(new Getter(descriptor, getter));
            } else {
                LOGGER.debug("Getter missing for field {}", descriptor.getName());
            }
        }

        return getters;
    }

    private static <T> void findBooleanGetters(Class<T> clazz, PropertyDescriptor descriptor) throws IntrospectionException {
        if (descriptor.getReadMethod() == null && Boolean.class.isAssignableFrom(descriptor.getPropertyType())) {
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

    private static boolean isAllParamsMatch(final Class<?>[] actualParams, final Class<?>[] wantedParams) {
        if (actualParams == null || wantedParams == null || actualParams.length != wantedParams.length) {
            return Boolean.FALSE;
        }
        boolean allMatch = Boolean.TRUE;
        for (int i = 0; i < actualParams.length; i++) {
            allMatch = allMatch && actualParams[i].isAssignableFrom(wantedParams[i]);
        }
        return allMatch;
    }

    private static boolean isParamMatch(final Class<?>[] actualParams, final Class<?> wantedParam) {
        if (actualParams != null && wantedParam != null && actualParams.length > 0) {
            for (Class<?> actualArg : actualParams) {
                if (actualArg.isAssignableFrom(wantedParam)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    private static boolean isPathRegexMatch(ClassCriteria classCriteria, File file) {
        String filePath = file.getAbsolutePath();
        for (String pathRegex : classCriteria.getExcludePathRegex()) {
            Matcher matcher = Pattern.compile(pathRegex).matcher(filePath);
            if (matcher.find()) {
                LOGGER.trace("Search criteria specifies to exclude paths containing regex {}, so skipping path {}", pathRegex, filePath);
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
