package ru.nullpointer.storefront.test.web.ui;

import java.math.BigDecimal;
import ru.nullpointer.storefront.test.*;
import org.junit.Test;
import ru.nullpointer.storefront.web.ui.DecimalEditor;
import static org.junit.Assert.*;

/**
 *
 * @author Alexander Yastrebov
 */
public class DecimalEditorTest {
    
    @Test
    public void testEditor() {
        DecimalEditor editor = new DecimalEditor(BigDecimal.class, true);
        
        editor.setAsText("0");
        assertEquals(new BigDecimal("0"), editor.getValue());
        
        editor.setAsText("0.00");
        assertEquals(new BigDecimal("0.00"), editor.getValue());
        
        editor.setAsText("123");
        assertEquals(new BigDecimal("123"), editor.getValue());
        
        editor.setAsText("123.0");
        assertEquals(new BigDecimal("123.0"), editor.getValue());
        
        editor.setAsText("123.");
        assertEquals(new BigDecimal("123"), editor.getValue());
        
        editor.setAsText("123,0");
        assertEquals(new BigDecimal("123.0"), editor.getValue());
        
        editor.setAsText("123,123");
        assertEquals(new BigDecimal("123.123"), editor.getValue());
    }
    
    @Test
    public void testGetAsText() {
        DecimalEditor editor = new DecimalEditor(BigDecimal.class, true);
        
        editor.setValue(new BigDecimal("0"));
        assertEquals("0", editor.getAsText());
        
        editor.setValue(new BigDecimal("0.00"));
        assertEquals("0", editor.getAsText());
        
        editor.setValue(new BigDecimal("123"));
        assertEquals("123", editor.getAsText());
        
        editor.setValue(new BigDecimal("123.0"));
        assertEquals("123", editor.getAsText());
        
        editor.setValue(new BigDecimal("123.0"));
        assertEquals("123", editor.getAsText());
        
        editor.setValue(new BigDecimal("123.7"));
        assertEquals("124", editor.getAsText());
    }
    
    @Test
    public void testEmpty() {
        DecimalEditor editor = new DecimalEditor(BigDecimal.class, true);
        
        editor.setAsText("");
        assertNull(editor.getValue());
        
        editor.setAsText("  ");
        assertNull(editor.getValue());
    }
}
