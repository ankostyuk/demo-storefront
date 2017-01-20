package ru.nullpointer.storefront.domain.support;

import java.util.Comparator;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Brand;

/**
 * @author ankostyuk
 */
public class BrandNameComparator implements Comparator<Brand> {

    @Override
    public int compare(Brand brand1, Brand brand2) {
        Assert.notNull(brand1);
        Assert.notNull(brand2);
        Assert.notNull(brand1.getName());
        Assert.notNull(brand2.getName());

        return brand1.getName().compareTo(brand2.getName());
    }
}
