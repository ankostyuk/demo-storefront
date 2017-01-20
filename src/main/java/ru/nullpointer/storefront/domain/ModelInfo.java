package ru.nullpointer.storefront.domain;

import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Вспомогательный класс для хранения информации о модели
 * @author Alexander Yastrebov
 */
public class ModelInfo {

    private Integer modelId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer offerCount;

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getOfferCount() {
        return offerCount;
    }

    public void setOfferCount(Integer offerCount) {
        this.offerCount = offerCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("modelId", modelId)//
                .append("minPrice", minPrice)//
                .append("maxPrice", maxPrice)//
                .append("offerCount", offerCount)//
                .toString();
    }
}
