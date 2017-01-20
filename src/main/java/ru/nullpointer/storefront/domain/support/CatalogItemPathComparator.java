package ru.nullpointer.storefront.domain.support;

import java.util.Comparator;
import java.util.List;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.CatalogItem;

/**
 * @author ankostyuk
 */
public class CatalogItemPathComparator implements Comparator<List<CatalogItem>> {

    private static final String PATH_SEPARATOR = "/";

    @Override
    public int compare(List<CatalogItem> path1, List<CatalogItem> path2) {
        Assert.notNull(path1);
        Assert.notNull(path2);

        // TODO некрасиво + используется сортировка строк, использовать CatalogItem.path?
        
        if (path1.isEmpty() || path2.isEmpty()) {
            return new Integer(path1.size()).compareTo(path2.size());
        }

        StringBuilder s1 = new StringBuilder(path1.get(0).getName());
        for (int i = 1; i < path1.size(); i ++) {
            s1.append(PATH_SEPARATOR).append(path1.get(i).getName());
        }
            
        StringBuilder s2 = new StringBuilder(path2.get(0).getName());
        for (int i = 1; i < path2.size(); i ++) {
            s2.append(PATH_SEPARATOR).append(path2.get(i).getName());
        }

        return s1.toString().compareTo(s2.toString());
    }
}
