package ru.nullpointer.storefront.service.search;

import java.util.Comparator;
import org.springframework.util.Assert;

/**
 * @author ankostyuk
 */
public class FragmentScoreComparator implements Comparator<Fragment> {
    @Override
    public int compare(Fragment f1, Fragment f2) {
        Assert.notNull(f1);
        Assert.notNull(f2);
            
        return Float.compare(f1.getScore(), f2.getScore());
    }
}
