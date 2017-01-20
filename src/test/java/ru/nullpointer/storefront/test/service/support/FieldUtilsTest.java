package ru.nullpointer.storefront.test.service.support;

import ru.nullpointer.storefront.test.*;
import org.junit.Test;
import ru.nullpointer.storefront.service.support.FieldUtils;
import static org.junit.Assert.*;

/**
 *
 * @author Alexander Yastrebov
 */
public class FieldUtilsTest {

    @Test
    public void testNullifyIfEmpty() {
        TestBean testBean = new TestBean();

        testBean.setName(null);
        FieldUtils.nullifyIfEmpty(testBean, "name");
        assertNull(testBean.getName());

        testBean.setName("");
        FieldUtils.nullifyIfEmpty(testBean, "name");
        assertNull(testBean.getName());

        testBean.setName("           ");
        FieldUtils.nullifyIfEmpty(testBean, "name");
        assertNull(testBean.getName());
    }

    @Test
    public void testIllegalNullifyIfEmpty() {
        TestBean testBean = null;

        try {
            testBean = new TestBean();
            testBean.setName("name");
            FieldUtils.nullifyIfEmpty(testBean, "illegal");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("name", testBean.getName());
        }

        try {
            testBean = new TestBean();
            testBean.setProtectedName("protected");
            FieldUtils.nullifyIfEmpty(testBean, "protectedName");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("protected", testBean.getProtectedName());
        }

        try {
            testBean = new TestBean();
            testBean.setProtectedName("private");
            FieldUtils.nullifyIfEmpty(testBean, "privateName");
            fail();
        } catch (IllegalArgumentException ex) {
            assertEquals("private", testBean.getProtectedName());
        }
    }

    public static class TestBean {

        private String name;
        private String protectedName;
        private String privateName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        protected String getProtectedName() {
            return protectedName;
        }

        protected void setProtectedName(String protectedName) {
            this.protectedName = protectedName;
        }

        private String getPrivateName() {
            return privateName;
        }

        private void setPrivateName(String privateName) {
            this.privateName = privateName;
        }
    }
}
