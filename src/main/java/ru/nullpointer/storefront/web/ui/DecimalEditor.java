package ru.nullpointer.storefront.web.ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

/**
 *
 * @author Alexander Yastrebov
 */
public class DecimalEditor extends CustomNumberEditor {

    public DecimalEditor(Class<?> clazz, boolean allowEmpty, int decimalCount) {
        super(clazz, new Format(decimalCount), allowEmpty);
    }

    public DecimalEditor(Class<?> clazz, boolean allowEmpty) {
        this(clazz, allowEmpty, 0);
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text != null) {
            text = text.replace('.', ',');
        }
        super.setAsText(text);
    }

    private static class Format extends DecimalFormat {

        private static final long serialVersionUID = 1L;

        public Format(int decimalCount) {
            super("#0." + StringUtils.rightPad("", decimalCount, '#'));
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(','); // TODO локаль? конфиг?
            symbols.setGroupingSeparator(' ');
            setDecimalFormatSymbols(symbols);
            setDecimalSeparatorAlwaysShown(false);
        }
    }
}
