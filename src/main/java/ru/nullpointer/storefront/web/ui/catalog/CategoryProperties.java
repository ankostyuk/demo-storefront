package ru.nullpointer.storefront.web.ui.catalog;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ru.nullpointer.storefront.domain.Category;

/**
 * Описывает свойства категории каталога
 * 
 * @author ankostyuk
 */
public class CategoryProperties extends ItemProperties {

    @NotNull(message = "{constraint.notnull}")
    private Integer parentItemId;

    @Valid
    private Category category;

    private Boolean paramCategory;

    @Override
    public Integer getParentItemId() {
        return parentItemId;
    }

    @Override
    public void setParentItemId(Integer parentItemId) {
        this.parentItemId = parentItemId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getParamCategory() {
        return paramCategory;
    }

    public void setParamCategory(Boolean paramCategory) {
        this.paramCategory = paramCategory;
    }
}
