package ru.nullpointer.storefront.domain.param;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author ankostyuk
 */
public class Param {

    private Integer id;
    private Integer parameterSetDescriptorId;
    private Integer paramGroupId;
    //
    @NotNull
    @Size.List({
        @Size(min = 1, message = "{constraint.size.min}"),
        @Size(max = 63, message = "{constraint.size.max}")
    })
    private String name;
    //
    @NotNull
    private String description;
    //
    private String columnName;
    private Type type;
    private Integer ordinal;
    //
    @NotNull
    private Boolean base;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParameterSetDescriptorId() {
        return parameterSetDescriptorId;
    }

    public void setParameterSetDescriptorId(Integer psdId) {
        this.parameterSetDescriptorId = psdId;
    }

    public Integer getParamGroupId() {
        return paramGroupId;
    }

    public void setParamGroupId(Integer paramGroupId) {
        this.paramGroupId = paramGroupId;
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

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public Boolean getBase() {
        return base;
    }

    public void setBase(Boolean base) {
        this.base = base;
    }

    public enum Type {

        BOOLEAN, NUMBER, SELECT
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("id", id)//
                .append("type", type)//
                .append("name", name)//
                .append("parameterSetDescriptorId", parameterSetDescriptorId)//
                .append("paramGroupId", paramGroupId)//
                .append("columnName", columnName)//
                .append("ordinal", ordinal)//
                .append("base", base)//
                .toString();
    }
}
