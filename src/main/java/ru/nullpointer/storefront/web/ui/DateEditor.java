package ru.nullpointer.storefront.web.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.springframework.beans.propertyeditors.CustomDateEditor;

/**
 *
 * @author Alexander Yastrebov
 */
public class DateEditor extends CustomDateEditor {

    public DateEditor(boolean allowEmpty) {
        // TODO: формат даты в конфиг
        super(getDateFormat(), allowEmpty);
    }

    public static DateFormat getDateFormat() {
        return new SimpleDateFormat("dd.MM.yyyy");
    }
}
