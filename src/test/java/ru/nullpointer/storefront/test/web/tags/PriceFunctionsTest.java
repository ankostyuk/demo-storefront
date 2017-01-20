package ru.nullpointer.storefront.test.web.tags;

import java.math.BigDecimal;
import ru.nullpointer.storefront.web.tags.PriceFunctions;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Alexander Yastrebov
 */
public class PriceFunctionsTest {

    @Test
    public void test_formatPrice() {
        assertNull(PriceFunctions.formatPrice(null, false));
        assertNull(PriceFunctions.formatPrice(null, true));

        assertEquals("0,93", PriceFunctions.formatPrice(new BigDecimal("0.9345"), false));
        assertEquals("0,94", PriceFunctions.formatPrice(new BigDecimal("0.9389"), false));
        assertEquals("0,99", PriceFunctions.formatPrice(new BigDecimal("0.99"), false));

        assertEquals("1", PriceFunctions.formatPrice(new BigDecimal("1"), false));
        assertEquals("1", PriceFunctions.formatPrice(new BigDecimal("1.0"), false));
        assertEquals("1", PriceFunctions.formatPrice(new BigDecimal("1.00"), false));
        assertEquals("1", PriceFunctions.formatPrice(new BigDecimal("1.001"), false));

        assertEquals("1,01", PriceFunctions.formatPrice(new BigDecimal("1.005"), false));
        assertEquals("1,01", PriceFunctions.formatPrice(new BigDecimal("1.01"), false));
        assertEquals("1,10", PriceFunctions.formatPrice(new BigDecimal("1.1"), false));
        assertEquals("1,12", PriceFunctions.formatPrice(new BigDecimal("1.123"), false));
        assertEquals("1,13", PriceFunctions.formatPrice(new BigDecimal("1.126"), false));

        assertEquals("1 234", PriceFunctions.formatPrice(new BigDecimal("1234.000"), false));
        assertEquals("1 234,01", PriceFunctions.formatPrice(new BigDecimal("1234.01"), false));

        assertEquals("1 234 567", PriceFunctions.formatPrice(new BigDecimal("1234567.0"), false));
        assertEquals("1 234 567,01", PriceFunctions.formatPrice(new BigDecimal("1234567.01"), false));

        // округление копеек
        assertEquals("99,12", PriceFunctions.formatPrice(new BigDecimal("99.12"), true));
        assertEquals("100", PriceFunctions.formatPrice(new BigDecimal("100.12"), true));
        assertEquals("101", PriceFunctions.formatPrice(new BigDecimal("100.78"), true));
        assertEquals("1 234 567", PriceFunctions.formatPrice(new BigDecimal("1234567.12"), true));
        assertEquals("1 234 568", PriceFunctions.formatPrice(new BigDecimal("1234567.89"), true));

        assertEquals("3,99", PriceFunctions.formatPrice(new BigDecimal("3.98520000"), true));
        assertEquals("4", PriceFunctions.formatPrice(new BigDecimal("3.99816000"), true));
    }
}
