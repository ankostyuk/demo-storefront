package ru.nullpointer.storefront.web.ui.catalog;

import javax.validation.Valid;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import ru.nullpointer.storefront.domain.CatalogItem;

/**
 * Описывает свойства элемента каталога
 * 
 * @author ankostyuk
 */
public class ItemProperties {
    @Valid
    private CatalogItem item;

    // is null - parent is root
    private Integer parentItemId;

    // is null - is last
    private Integer afterItemId;

    // for grouping operations
    private Boolean canChoose;

    private Integer offerCount;

    private Integer level;
    private Boolean canDelete;
    private Boolean canActive;

    public CatalogItem getItem() {
        return item;
    }

    public void setItem(CatalogItem item) {
        this.item = item;
    }

    public Integer getParentItemId() {
        return parentItemId;
    }

    public void setParentItemId(Integer parentItemId) {
        this.parentItemId = parentItemId;
    }

    public Integer getAfterItemId() {
        return afterItemId;
    }

    public void setAfterItemId(Integer afterItemId) {
        this.afterItemId = afterItemId;
    }

    public Boolean getCanChoose() {
        return canChoose;
    }

    public void setCanChoose(Boolean canChoose) {
        this.canChoose = canChoose;
    }

    public Integer getOfferCount() {
        return offerCount;
    }

    public void setOfferCount(Integer offerCount) {
        this.offerCount = offerCount;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }

    public Boolean getCanActive() {
        return canActive;
    }

    public void setCanActive(Boolean canActive) {
        this.canActive = canActive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("item", item)//
                .append("parentItemId", parentItemId)//
                .append("afterItemId", afterItemId)//
                .append("canChoose", canChoose)//
                .append("offerCount", offerCount)//
                .append("level", level)//
                .append("canDelete", canDelete)//
                .append("canActive", canActive)//
                .toString();
    }
}
