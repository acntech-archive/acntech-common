package no.acntech.common.test;

import org.junit.Test;

import no.acntech.common.test.objects.ObjectType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestReflectionUtilsTest {

    @Test
    public void testSetInternalFieldUsingNull() throws Exception {
        try {
            TestReflectionUtils.setInternalField(null, "whatever", "whatever");

            fail("Should throw exception");
        } catch (Exception e) {
            assertTrue("Exception is of wrong type", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testSetInternalField() throws Exception {
        ObjectType objectType = new ObjectType();
        TestReflectionUtils.setInternalField(objectType, "str", "1337");

        assertEquals("1337", objectType.getStr());
    }

    @Test
    public void testFindClassesInPackage() throws Exception {
        Class<?>[] classes = TestReflectionUtils.findClasses(TestReflectionUtils.class.getPackage());

        assertNotNull(classes);
        assertTrue(classes.length > 0);
    }
}