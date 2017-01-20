package ru.nullpointer.storefront.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Alexander Yastrebov
 */
public class Contact {

    private Integer id;
    private Integer accountId;
    //
    @NotNull(message = "{constraint.notnull}")
    private Type type;
    //
    @NotNull
    @Size(min = 3, max = 63, message = "{constraint.size}")
    private String value;
    //
    @Size(max = 255, message = "{constraint.size.max}")
    private String label;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public enum Type {

        PHONE, EMAIL, ICQ, SKYPE
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("id", id)//
                .append("accountId", accountId)//
                .append("type", type)//
                .append("value", value)//
                .append("label", label)//
                .toString();
    }
}
