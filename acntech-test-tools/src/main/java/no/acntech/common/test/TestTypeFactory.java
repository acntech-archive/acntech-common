package no.acntech.common.test;

import javax.xml.datatype.DatatypeConfigurationException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

public final class TestTypeFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestTypeFactory.class);
    private static final List<BasicType> BASIC_TYPES = new ArrayList<>();

    static {
        populateBasicTypes();
    }

    private TestTypeFactory() {
    }

    public static Object createType(Class<?> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException, DatatypeConfigurationException {
        if (clazz == null) {
            throw new IllegalArgumentException("Input class is null");
        }

        Object object = createBasicType(clazz);
        if (object != null) {
            return object;
        }

        object = createMockType(clazz);
        if (object != null) {
            return object;
        }

        object = createBeanType(clazz);
        if (object != null) {
            return object;
        }

        fail("Could not create object for class " + clazz.getName() + ". Add custom types by using " + TestTypeFactory.class.getName() + ".addBasicType(BasicType basicType)");
        return null;
    }

    private static <T> Object createBasicType(Class<T> clazz) {
        for (BasicType basicType : BASIC_TYPES) {
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

    private static Object createBeanType(Class<?> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        try {
            return TestReflectionUtils.createBean(clazz);
        } catch (Exception e) {
            LOGGER.warn("Unable to create bean from class " + clazz.getName(), e);
            return null;
        }
    }

    public static Class<?>[] getClassesForObjects(Object... objects) {
        if (objects == null) {
            return new Class[0];
        }

        Class<?>[] argClasses = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            argClasses[i] = objects[i].getClass();
        }
        return argClasses;
    }

    /**
     * Adds a custom type to the type factory for use with the test utils.
     *
     * @param basicType Custom type to add.
     */
    public static void addBasicType(BasicType basicType) {
        BASIC_TYPES.add(basicType);
    }

    private static void populateBasicTypes() {

        // Object
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return Object.class == clazz;
            }

            @Override
            public Object getType(Class<?> clazz) {
                return new Object();
            }
        });

        // Class
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return Class.class == clazz;
            }

            @Override
            public Object getType(Class<?> clazz) {
                return clazz;
            }
        });

        // String
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return String.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getType(Class<?> clazz) {
                return "whatever";
            }
        });

        // Boolean
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return boolean.class == clazz || Boolean.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getType(Class<?> clazz) {
                return Boolean.TRUE;
            }
        });

        // Integer
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return int.class == clazz || Integer.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getType(Class<?> clazz) {
                return 1337;
            }
        });

        // Long
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return long.class == clazz || Long.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getType(Class<?> clazz) {
                return 1337L;
            }
        });

        // Double
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return double.class == clazz || Double.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getType(Class<?> clazz) {
                return 13.37D;
            }
        });

        // Float
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return float.class == clazz || Float.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getType(Class<?> clazz) {
                return 13.37F;
            }
        });

        // Character
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return char.class == clazz || Character.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getType(Class<?> clazz) {
                return 'Y';
            }
        });

        // List
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return List.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getType(Class<?> clazz) {
                return new ArrayList<>();
            }
        });

        // Array
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return clazz.isArray();
            }

            @Override
            public Object getType(Class<?> clazz) {
                return Array.newInstance(clazz.getComponentType(), 1);
            }
        });

        // Enum
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return clazz.isEnum();
            }

            @Override
            public Object getType(Class<?> clazz) {
                return clazz.getEnumConstants()[0];
            }
        });

        // java.lang.Date
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return Date.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getType(Class<?> clazz) {
                return new Date(System.currentTimeMillis());
            }
        });

        // java.lang.Calendar
        addBasicType(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return Calendar.class.isAssignableFrom(clazz);
            }

            @Override
            public Object getType(Class<?> clazz) {
                return Calendar.getInstance();
            }
        });
    }
}
