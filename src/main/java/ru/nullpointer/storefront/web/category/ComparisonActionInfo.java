package ru.nullpointer.storefront.web.category;

import ru.nullpointer.storefront.web.support.ActionInfo;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Category;

/**
 * @author ankostyuk
 */
public class ComparisonActionInfo extends ActionInfo {

    private Category category;

    public ComparisonActionInfo(Type type, Category category) {
        super(type);
        Assert.notNull(category);
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
