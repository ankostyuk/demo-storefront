package ru.nullpointer.storefront.domain;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Описывает свойства категории каталога
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class Category {

    private Integer id;
    //
    @NotNull(message = "{constraint.notnull}")
    private Integer unitId;
    //
    private Integer parameterSetDescriptorId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public Integer getParameterSetDescriptorId() {
        return parameterSetDescriptorId;
    }

    public void setParameterSetDescriptorId(Integer psdId) {
        this.parameterSetDescriptorId = psdId;
    }

    public boolean isParametrized() {
        return (parameterSetDescriptorId != null);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("id", id)//
                .append("unitId", unitId)//
                .append("parameterSetDescriptorId", parameterSetDescriptorId)//
                .toString();
    }
}
