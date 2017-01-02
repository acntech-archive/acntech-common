package no.acntech.common.test;

import org.junit.Test;

import java.util.Arrays;

public class TestReflectionUtilsTest {

    @Test
    public void testPackage() throws Exception {
        Class<?>[] classes = TestReflectionUtils.findClasses(TestReflectionUtils.class.getPackage());
        Arrays.stream(classes).forEach(System.out::println);
    }
}