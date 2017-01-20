package ru.nullpointer.storefront.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class Unit {

    private Integer id;
    
    @NotNull
    @Size.List({
        @Size(min = 1, message = "{constraint.size.min}"),
        @Size(max = 63, message = "{constraint.size.max}")
    })
    private String name;
    
    @Size.List({
        @Size(max = 15, message = "{constraint.size.max}")
    })
    private String abbreviation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("id", id)//
                .append("name", name)//
                .append("abbreviation", abbreviation)//
                .toString();
    }
}
// TODO разобраться с abbreviation (обязательность <-> вывод при просмотре товарных предложений)