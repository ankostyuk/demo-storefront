package ru.nullpointer.storefront.domain;

import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Alexander
 */
public class CurrencyRate {

    private String fromCurrency;
    private BigDecimal fromRate;
    private String toCurrency;
    private BigDecimal toRate;

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public BigDecimal getFromRate() {
        return fromRate;
    }

    public void setFromRate(BigDecimal fromRate) {
        this.fromRate = fromRate;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getToRate() {
        return toRate;
    }

    public void setToRate(BigDecimal toRate) {
        this.toRate = toRate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("fromCurrency", fromCurrency)//
                .append("fromRate", fromRate)//
                .append("toCurrency", toCurrency)//
                .append("toRate", toRate)//
                .toString();
    }
}
