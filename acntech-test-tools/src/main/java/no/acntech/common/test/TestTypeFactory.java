package no.acntech.common.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class TestTypeFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestTypeFactory.class);
    private static final List<BasicType> TYPES = new ArrayList<>();

    static {
        populateTypes();
    }

    private TestTypeFactory() {
    }

    /**
     * Adds a custom type to the type factory for use with the test utils.
     *
     * @param type Custom type to add.
     */
    public static void addBasicType(BasicType type) {
        TYPES.add(type);
    }

    /**
     * Create an object for given class.
     *
     * @param clazz Class for which to create an object.
     * @return Created object.
     * @throws InstantiationException       If object can not be instantiated.
     * @throws ObjectInstantiationException If object could not be instantiated for class.
     */
    public static <T> T createType(Class<T> clazz) throws InstantiationException {
        if (clazz == null) {
            throw new IllegalArgumentException("Input class is null");
        }

        T object = createBasicType(clazz);
        if (object != null) {
            return object;
        }

        object = AdvancedTestTypeFactory.createType(clazz);
        if (object != null) {
            return object;
        }

        object = createMockTypeWithMockito(clazz);
        if (object != null) {
            return object;
        }

        object = createMockTypeWithEasyMock(clazz);
        if (object != null) {
            return object;
        }

        object = createBeanType(clazz);
        if (object != null) {
            return object;
        }

        throw new ObjectInstantiationException(clazz);
    }

    @SuppressWarnings("unchecked")
    private static <T> T createBasicType(Class<T> clazz) {
        for (BasicType type : TYPES) {
            if (type.isType(clazz)) {
                return (T) type.getType(clazz);
            }
        }
        return null;
    }

    private static <T> T createMockTypeWithMockito(Class<T> clazz) {
        if (TestReflectionUtils.isFinalClass(clazz)) {
            LOGGER.warn("Can not mock final classes with Mockito");
            return null;
        } else if (!TestReflectionUtils.isClassExists("org.mockito.Mockito", TestTypeFactory.class.getClassLoader())) {
            LOGGER.warn("Can not find Mockito on classpath");
            return null;
        } else {
            return org.mockito.Mockito.mock(clazz);
        }
    }

    private static <T> T createMockTypeWithEasyMock(Class<T> clazz) {
        if (TestReflectionUtils.isFinalClass(clazz)) {
            LOGGER.warn("Can not mock final classes with EasyMock");
            return null;
        } else if (!TestReflectionUtils.isClassExists("org.easymock.EasyMock", TestTypeFactory.class.getClassLoader())) {
            LOGGER.warn("Can not find EasyMock on classpath");
            return null;
        } else {
            return org.easymock.EasyMock.mock(clazz);
        }
    }

    private static <T> T createBeanType(Class<T> clazz) throws InstantiationException {
        try {
            return TestReflectionUtils.createBean(clazz);
        } catch (Exception e) {
            LOGGER.warn("Unable to create bean from class " + clazz.getName(), e);
            return null;
        }
    }

    static Class<?>[] getClassesForObjects(Object... objects) {
        if (objects == null) {
            return new Class[0];
        }

        Class<?>[] argClasses = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            argClasses[i] = objects[i].getClass();
        }
        return argClasses;
    }

    private static void populateTypes() {

        // Object
        addBasicType(new BasicType<Object>() {
            @Override
            public boolean isType(Class<Object> clazz) {
                return Object.class == clazz;
            }

            @Override
            public Object getType(Class<Object> clazz) {
                return new Object();
            }
        });

        // Class
        addBasicType(new BasicType<Class>() {
            @Override
            public boolean isType(Class<Class> clazz) {
                return Class.class == clazz;
            }

            @Override
            public Class getType(Class<Class> clazz) {
                return clazz;
            }
        });

        // String
        addBasicType(new BasicType<String>() {
            @Override
            public boolean isType(Class<String> clazz) {
                return String.class.isAssignableFrom(clazz);
            }

            @Override
            public String getType(Class<String> clazz) {
                return "whatever";
            }
        });

        // Boolean
        addBasicType(new BasicType<Boolean>() {
            @Override
            public boolean isType(Class<Boolean> clazz) {
                return boolean.class == clazz || Boolean.class.isAssignableFrom(clazz);
            }

            @Override
            public Boolean getType(Class<Boolean> clazz) {
                return Boolean.TRUE;
            }
        });

        // Integer
        addBasicType(new BasicType<Integer>() {
            @Override
            public boolean isType(Class<Integer> clazz) {
                return int.class == clazz || Integer.class.isAssignableFrom(clazz);
            }

            @Override
            public Integer getType(Class<Integer> clazz) {
                return 1337;
            }
        });

        // Long
        addBasicType(new BasicType<Long>() {
            @Override
            public boolean isType(Class<Long> clazz) {
                return long.class == clazz || Long.class.isAssignableFrom(clazz);
            }

            @Override
            public Long getType(Class<Long> clazz) {
                return 1337L;
            }
        });

        // Double
        addBasicType(new BasicType<Double>() {
            @Override
            public boolean isType(Class<Double> clazz) {
                return double.class == clazz || Double.class.isAssignableFrom(clazz);
            }

            @Override
            public Double getType(Class<Double> clazz) {
                return 13.37D;
            }
        });

        // Float
        addBasicType(new BasicType<Float>() {
            @Override
            public boolean isType(Class<Float> clazz) {
                return float.class == clazz || Float.class.isAssignableFrom(clazz);
            }

            @Override
            public Float getType(Class<Float> clazz) {
                return 13.37F;
            }
        });

        // Character
        addBasicType(new BasicType<Character>() {
            @Override
            public boolean isType(Class<Character> clazz) {
                return char.class == clazz || Character.class.isAssignableFrom(clazz);
            }

            @Override
            public Character getType(Class<Character> clazz) {
                return 'Y';
            }
        });

        // List
        addBasicType(new BasicType<List>() {
            @Override
            public boolean isType(Class<List> clazz) {
                return List.class.isAssignableFrom(clazz);
            }

            @Override
            public List<?> getType(Class<List> clazz) {
                return new ArrayList<>();
            }
        });

        // Array
        addBasicType(new BasicType<Object>() {
            @Override
            public boolean isType(Class<Object> clazz) {
                return clazz.isArray();
            }

            @Override
            public Object getType(Class<Object> clazz) {
                return Array.newInstance(clazz.getComponentType(), 1);
            }
        });

        // Enum
        addBasicType(new BasicType<Object>() {
            @Override
            public boolean isType(Class<Object> clazz) {
                return clazz.isEnum();
            }

            @Override
            public Object getType(Class<Object> clazz) {
                return clazz.getEnumConstants()[0];
            }
        });

        // java.lang.Date
        addBasicType(new BasicType<Date>() {
            @Override
            public boolean isType(Class<Date> clazz) {
                return Date.class.isAssignableFrom(clazz);
            }

            @Override
            public Date getType(Class<Date> clazz) {
                return new Date(System.currentTimeMillis());
            }
        });

        // java.lang.Calendar
        addBasicType(new BasicType<Calendar>() {
            @Override
            public boolean isType(Class<Calendar> clazz) {
                return Calendar.class.isAssignableFrom(clazz);
            }

            @Override
            public Calendar getType(Class<Calendar> clazz) {
                return Calendar.getInstance();
            }
        });
    }
}
