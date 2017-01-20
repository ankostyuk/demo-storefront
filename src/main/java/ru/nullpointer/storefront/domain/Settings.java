package ru.nullpointer.storefront.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Alexander
 */
public class Settings implements Serializable {

    private static final long serialVersionUID = 18062010;
    //
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    //
    private Integer id;
    //
    private Integer regionId;
    private boolean regionAware;
    //
    private PRICE_TYPE priceType;
    private String extraCurrency;
    private int pageSize;
    //
    private transient BigDecimal extraCurrencyMultiplier;
    private transient Region region;

    public Settings() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public boolean isRegionAware() {
        return regionAware;
    }

    public void setRegionAware(boolean regionAware) {
        this.regionAware = regionAware;
    }

    public PRICE_TYPE getPriceType() {
        return priceType;
    }

    public void setPriceType(PRICE_TYPE priceType) {
        this.priceType = priceType;
    }

    public String getExtraCurrency() {
        return extraCurrency;
    }

    public void setExtraCurrency(String extraCurrency) {
        this.extraCurrency = extraCurrency;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public BigDecimal getExtraCurrencyMultiplier() {
        return extraCurrencyMultiplier;
    }

    public void setExtraCurrencyMultiplier(BigDecimal extraCurrencyMultiplier) {
        this.extraCurrencyMultiplier = extraCurrencyMultiplier;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("id", id)//
                .append("regionId", regionId)//
                .append("region", region)//
                .append("regionAware", regionAware)//
                .append("priceType", priceType)//
                .append("extraCurrency", extraCurrency)//
                .append("pageSize", pageSize)//
                .toString();
    }

    public static enum PRICE_TYPE {

        DEFAULT, EXTRA_CURRENCY
    }
}
