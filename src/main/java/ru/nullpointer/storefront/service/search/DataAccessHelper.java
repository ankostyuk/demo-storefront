package ru.nullpointer.storefront.service.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.domain.CatalogItem;

/**
 * @author ankostyuk
 */
@Component
public class DataAccessHelper {

    @Resource
    private DataAccess dataAccess;

    public Set<Integer> catalogItemListToIdSet(List<CatalogItem> itemList) {
        Set<Integer> idSet = new HashSet<Integer>(itemList.size());
        for (CatalogItem item : itemList) {
            idSet.add(item.getId());
        }
        return idSet;
    }

    public void shortCatalogItemPath(List<CatalogItem> path) {
        path.remove(path.size() - 1);
    }

    public Set<Integer> getCatalogItemPathIdSet(Integer itemId, boolean full) {
        List<CatalogItem> path = dataAccess.getCatalogItemPath(itemId);
        if (!full) {
            shortCatalogItemPath(path);
        }
        return catalogItemListToIdSet(path);
    }

    public List<CatalogItem> getCatalogItemPath(Integer itemId, boolean full) {
        List<CatalogItem> path = dataAccess.getCatalogItemPath(itemId);
        if (!full) {
            shortCatalogItemPath(path);
        }
        return path;
    }
}
