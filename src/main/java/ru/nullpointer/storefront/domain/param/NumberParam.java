package ru.nullpointer.storefront.domain.param;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Alexander Yastrebov
 */
public class NumberParam extends Param {

    //
    // Точность минимального и максимального значений равна (12,4)
    // т.е. 8 разрядов до запятой и 4 разряда после.
    //
    @NotNull(message = "{constraint.notnull}")
    @DecimalMin(value = "-99999999", message = "{constraint.decimalmin}")
    @DecimalMax(value = "99999999", message = "{constraint.decimalmax}")
    private BigDecimal minValue;
    //
    @NotNull(message = "{constraint.notnull}")
    @DecimalMin(value = "-99999999", message = "{constraint.decimalmin}")
    @DecimalMax(value = "99999999", message = "{constraint.decimalmax}")
    private BigDecimal maxValue;
    //
    @NotNull(message = "{constraint.notnull}")
    private Integer unitId;
    //
    @NotNull(message = "{constraint.notnull}")
    @Min(value = 0, message = "{constraint.min}")
    @Max(value = 4, message = "{constraint.max}")
    private Integer precision;

    public NumberParam() {
        setType(Type.NUMBER);
    }

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("minValue", minValue)//
                .append("maxValue", maxValue)//
                .append("unitId", unitId)//
                .append("precision", precision)//
                .toString();
    }
}
