package ru.nullpointer.storefront.test.web.ui;

import ru.nullpointer.storefront.test.*;
import org.junit.Test;
import ru.nullpointer.storefront.web.ui.EnumEditor;
import static org.junit.Assert.*;

/**
 *
 * @author Alexander Yastrebov
 */
public class EnumEditorTest {

    enum TEST_ENUM {

        ONE, TWO, THREE
    }

    @Test
    public void testAllowEmpty() {
        EnumEditor editor = new EnumEditor(TEST_ENUM.class, true);

        editor.setAsText("ONE");
        assertEquals(editor.getValue(), TEST_ENUM.ONE);
        assertEquals(editor.getAsText(), "ONE");

        editor.setAsText(null);
        assertNull(editor.getValue());
        assertEquals(editor.getAsText(), "");

        editor.setAsText("");
        assertNull(editor.getValue());
        assertEquals(editor.getAsText(), "");

        editor.setAsText("    ");
        assertNull(editor.getValue());
        assertEquals(editor.getAsText(), "");
    }

    @Test
    public void testNotAllowEmpty() {
        EnumEditor editor = new EnumEditor(TEST_ENUM.class, false);

        editor.setAsText("TWO");
        assertEquals(editor.getValue(), TEST_ENUM.TWO);

        try {
            editor.setAsText(null);
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            editor.setAsText("");
            fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            editor.setAsText("    ");
            fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid() {
        EnumEditor editor = new EnumEditor(TEST_ENUM.class);

        editor.setAsText("ILLEGAL");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConstructor() {
        new EnumEditor(null, false);
    }
}
