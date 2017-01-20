package ru.nullpointer.storefront.service.search;

import org.springframework.util.Assert;

/**
 * @author ankostyuk
 */
public class IdentityFieldValue {

    private static final String PART_SEPARATOR = "@";
    private static final int ID_RADIX = 10;

    private Type type;
    private Integer id;
    private String value;

    public IdentityFieldValue(Type type, Integer id) {
        Assert.notNull(type);
        Assert.notNull(id);
        value = new StringBuilder(type.toString())
                .append(PART_SEPARATOR)
                .append(Integer.toString(id, ID_RADIX))
                .toString();
        this.type = type;
        this.id = id;
    }

    public IdentityFieldValue(String value) {
        Assert.hasText(value);
        String[] parts = value.split(PART_SEPARATOR);
        type = Type.valueOf(parts[0]);
        id = Integer.parseInt(parts[1], ID_RADIX);
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
