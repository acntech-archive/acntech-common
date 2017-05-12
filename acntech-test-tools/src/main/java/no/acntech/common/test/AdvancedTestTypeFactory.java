package no.acntech.common.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

final class AdvancedTestTypeFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedTestTypeFactory.class);
    private static final List<BasicType> TYPES = new ArrayList<>();

    static {
        populateTypes();
    }

    private AdvancedTestTypeFactory() {
    }

    @SuppressWarnings("unchecked")
    static <T> T createType(Class<T> clazz) throws InstantiationException {
        if (clazz == null) {
            throw new IllegalArgumentException("Input class is null");
        }

        for (BasicType type : TYPES) {
            if (type.isType(clazz)) {
                return (T) type.getType(clazz);
            }
        }
        return null;
    }

    private static void addType(BasicType type) {
        TYPES.add(type);
    }

    private static void populateTypes() {

        // Java Time (JSR-310)
        populateJavaTimeTypes();

        // Joda-Time API
        populateJodaTimeTypes();
    }

    private static void populateJavaTimeTypes() {
        if (TestReflectionUtils.isClassExists("java.time.LocalDate", AdvancedTestTypeFactory.class.getClassLoader())) {

            // Time
            addType(new BasicType<java.time.LocalTime>() {
                @Override
                public boolean isType(Class<java.time.LocalTime> clazz) {
                    return java.time.LocalTime.class.isAssignableFrom(clazz);
                }

                @Override
                public java.time.LocalTime getType(Class<java.time.LocalTime> clazz) {
                    return java.time.LocalTime.now();
                }
            });

            // Date
            addType(new BasicType<java.time.LocalDate>() {
                @Override
                public boolean isType(Class<java.time.LocalDate> clazz) {
                    return java.time.LocalDate.class.isAssignableFrom(clazz);
                }

                @Override
                public java.time.LocalDate getType(Class<java.time.LocalDate> clazz) {
                    return java.time.LocalDate.now();
                }
            });

            // Datetime
            addType(new BasicType<java.time.LocalDateTime>() {
                @Override
                public boolean isType(Class<java.time.LocalDateTime> clazz) {
                    return java.time.LocalDateTime.class.isAssignableFrom(clazz);
                }

                @Override
                public java.time.LocalDateTime getType(Class<java.time.LocalDateTime> clazz) {
                    return java.time.LocalDateTime.now();
                }
            });

            // Zoned datetime
            addType(new BasicType<java.time.ZonedDateTime>() {
                @Override
                public boolean isType(Class<java.time.ZonedDateTime> clazz) {
                    return java.time.ZonedDateTime.class.isAssignableFrom(clazz);
                }

                @Override
                public java.time.ZonedDateTime getType(Class<java.time.ZonedDateTime> clazz) {
                    return java.time.ZonedDateTime.now();
                }
            });

            // Offset time
            addType(new BasicType<java.time.OffsetTime>() {
                @Override
                public boolean isType(Class<java.time.OffsetTime> clazz) {
                    return java.time.OffsetTime.class.isAssignableFrom(clazz);
                }

                @Override
                public java.time.OffsetTime getType(Class<java.time.OffsetTime> clazz) {
                    return java.time.OffsetTime.now();
                }
            });

            // That time of the month
            addType(new BasicType<java.time.Period>() {
                @Override
                public boolean isType(Class<java.time.Period> clazz) {
                    return java.time.Period.class.isAssignableFrom(clazz);
                }

                @Override
                public java.time.Period getType(Class<java.time.Period> clazz) {
                    return java.time.Period.ofDays(1337);
                }
            });

            // Duration
            addType(new BasicType<java.time.Duration>() {
                @Override
                public boolean isType(Class<java.time.Duration> clazz) {
                    return java.time.Duration.class.isAssignableFrom(clazz);
                }

                @Override
                public java.time.Duration getType(Class<java.time.Duration> clazz) {
                    return java.time.Duration.ofDays(1337);
                }
            });

        } else {
            LOGGER.debug("Classes for Java Time (JSR-310) could not be found on classpath. Possibly running JVM version < 1.8");
        }
    }

    private static void populateJodaTimeTypes() {
        if (TestReflectionUtils.isClassExists("org.joda.time.LocalDate", AdvancedTestTypeFactory.class.getClassLoader())) {

            // Time
            addType(new BasicType<org.joda.time.LocalTime>() {
                @Override
                public boolean isType(Class<org.joda.time.LocalTime> clazz) {
                    return org.joda.time.LocalTime.class.isAssignableFrom(clazz);
                }

                @Override
                public org.joda.time.LocalTime getType(Class<org.joda.time.LocalTime> clazz) {
                    return org.joda.time.LocalTime.now();
                }
            });

            // Date
            addType(new BasicType<org.joda.time.LocalDate>() {
                @Override
                public boolean isType(Class<org.joda.time.LocalDate> clazz) {
                    return org.joda.time.LocalDate.class.isAssignableFrom(clazz);
                }

                @Override
                public org.joda.time.LocalDate getType(Class<org.joda.time.LocalDate> clazz) {
                    return org.joda.time.LocalDate.now();
                }
            });

            // Datetime
            addType(new BasicType<org.joda.time.LocalDateTime>() {
                @Override
                public boolean isType(Class<org.joda.time.LocalDateTime> clazz) {
                    return org.joda.time.LocalDateTime.class.isAssignableFrom(clazz);
                }

                @Override
                public org.joda.time.LocalDateTime getType(Class<org.joda.time.LocalDateTime> clazz) {
                    return org.joda.time.LocalDateTime.now();
                }
            });

            // Zoned datetime
            addType(new BasicType<org.joda.time.DateTime>() {
                @Override
                public boolean isType(Class<org.joda.time.DateTime> clazz) {
                    return org.joda.time.DateTime.class.isAssignableFrom(clazz);
                }

                @Override
                public org.joda.time.DateTime getType(Class<org.joda.time.DateTime> clazz) {
                    return org.joda.time.DateTime.now();
                }
            });

            // Offset time
            addType(new BasicType<org.joda.time.Instant>() {
                @Override
                public boolean isType(Class<org.joda.time.Instant> clazz) {
                    return org.joda.time.Instant.class.isAssignableFrom(clazz);
                }

                @Override
                public org.joda.time.Instant getType(Class<org.joda.time.Instant> clazz) {
                    return org.joda.time.Instant.now();
                }
            });

            // That time of the month
            addType(new BasicType<org.joda.time.Period>() {
                @Override
                public boolean isType(Class<org.joda.time.Period> clazz) {
                    return org.joda.time.Period.class.isAssignableFrom(clazz);
                }

                @Override
                public org.joda.time.Period getType(Class<org.joda.time.Period> clazz) {
                    return org.joda.time.Period.days(1337);
                }
            });

            // Duration
            addType(new BasicType<org.joda.time.Duration>() {
                @Override
                public boolean isType(Class<org.joda.time.Duration> clazz) {
                    return org.joda.time.Duration.class.isAssignableFrom(clazz);
                }

                @Override
                public org.joda.time.Duration getType(Class<org.joda.time.Duration> clazz) {
                    return org.joda.time.Duration.standardDays(1337);
                }
            });

            // Duration
            addType(new BasicType<org.joda.time.Interval>() {
                @Override
                public boolean isType(Class<org.joda.time.Interval> clazz) {
                    return org.joda.time.Interval.class.isAssignableFrom(clazz);
                }

                @Override
                public org.joda.time.Interval getType(Class<org.joda.time.Interval> clazz) {
                    return org.joda.time.Interval.parse("1970-01-01T00:00:00");
                }
            });

        } else {
            LOGGER.debug("Classes for Joda-Time API could not be found on classpath");
        }
    }
}
