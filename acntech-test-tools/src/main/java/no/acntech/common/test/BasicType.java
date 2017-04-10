package no.acntech.common.test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

abstract class BasicType implements Type {

    abstract boolean isType(Class<?> clazz);

    static List<BasicType> getBasicTypes() {

        final List<BasicType> basicTypes = new ArrayList<>();

        // String
        basicTypes.add(new BasicType() {
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
        basicTypes.add(new BasicType() {
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
        basicTypes.add(new BasicType() {
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
        basicTypes.add(new BasicType() {
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
        basicTypes.add(new BasicType() {
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
        basicTypes.add(new BasicType() {
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
        basicTypes.add(new BasicType() {
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
        basicTypes.add(new BasicType() {
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
        basicTypes.add(new BasicType() {
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
        basicTypes.add(new BasicType() {
            @Override
            public boolean isType(Class<?> clazz) {
                return clazz.isEnum();
            }

            @Override
            public Object getType(Class<?> clazz) {
                return clazz.getEnumConstants()[0];
            }
        });

        return basicTypes;
    }
}
