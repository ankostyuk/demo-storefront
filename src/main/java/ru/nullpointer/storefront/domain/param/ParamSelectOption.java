package ru.nullpointer.storefront.domain.param;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author ankostyuk
 */
public class ParamSelectOption {

    private Integer id;
    private Integer paramId;
    //
    @NotNull
    @Size.List({
        @Size(min = 1, message = "{constraint.size.min}"),
        @Size(max = 63, message = "{constraint.size.max}")
    })
    private String name;
    //
    private Integer ordinal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParamId() {
        return paramId;
    }

    public void setParamId(Integer psdId) {
        this.paramId = psdId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("id", id)//
                .append("paramId", paramId)//
                .append("name", name)//
                .append("ordinal", ordinal)//
                .toString();
    }
}
