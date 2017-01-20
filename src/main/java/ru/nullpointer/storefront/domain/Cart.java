package ru.nullpointer.storefront.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Описывает список покупок
 * @author Alexander Yastrebov
 */
public class Cart {

    public static final int MAX_CART_ITEM_LIST_SIZE = 100;
    //
    private Integer id;
    private Integer sessionId;
    //
    @NotNull
    @Size(min = 1, max = 63, message = "{constraint.size}")
    private String name;
    //
    @Size(min = 0, max = 255, message = "{constraint.size.max}")
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("id", id)//
                .append("sessionId", sessionId)//
                .append("name", name)//
                .append("description", description)//
                .toString();
    }
}
