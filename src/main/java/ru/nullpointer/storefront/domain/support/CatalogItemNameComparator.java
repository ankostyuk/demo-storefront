package ru.nullpointer.storefront.domain.support;

import java.util.Comparator;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.CatalogItem;

/**
 * @author ankostyuk
 */
public class CatalogItemNameComparator implements Comparator<CatalogItem> {

    @Override
    public int compare(CatalogItem item1, CatalogItem item2) {
        Assert.notNull(item1);
        Assert.notNull(item2);

        return item1.getName().compareTo(item2.getName());
    }
}
