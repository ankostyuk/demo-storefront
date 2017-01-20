package ru.nullpointer.storefront.web.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.domain.support.AbstractMatch;
import ru.nullpointer.storefront.domain.support.CatalogItemNameComparator;
import ru.nullpointer.storefront.domain.support.ModelMatch;
import ru.nullpointer.storefront.domain.support.OfferMatch;
import ru.nullpointer.storefront.service.CatalogService;
import ru.nullpointer.storefront.service.UnitService;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Component
public class CatalogHelper {

    @Resource
    private CatalogService catalogService;
    @Resource
    private UnitService unitService;

    public Map<Integer, List<CatalogItem>> buildMatchCategoryPathMap(Collection<AbstractMatch> matchList) {
        Assert.notNull(matchList);
        Set<Integer> categoryIdSet = buildMatchCategoryIdSet(matchList);
        return catalogService.getCategoryPathMap(categoryIdSet);
    }

    /**
     * Возвращает путь относительно <code>item</code> на основе <code>path</code>.
     * @return список-путь относительно <code>item</code>,
     * список может быть пустым, если <code>item</code> последний элемент в <code>path</code>.
     * <code>null</code> если нет пересечения <code>item</code> и <code>path</code>.
     */
    public List<CatalogItem> relativePath(List<CatalogItem> path, CatalogItem item) {
        Assert.notNull(path);
        Assert.notNull(item);

        int size = path.size();

        for (int i = 0; i < size; i++) {
            if (path.get(i).getId().equals(item.getId())) {
                if (i < size - 1) {
                    return path.subList(i + 1, size);
                } else {
                    return Collections.emptyList();
                }
            }
        }

        return null;
    }

    public class RootSectionRetriever {
        private List<CatalogItem> rootSectionList;
        private Map<Integer, List<CatalogItem>> rootSectionCategoryMap;

        /**
         * Возвращает список разделов верхнего уровня.
         */
        public List<CatalogItem> getRootSectionList() {
            return rootSectionList;
        }

        protected void setRootSectionList(List<CatalogItem> rootSectionList) {
            this.rootSectionList = rootSectionList;
        }

        /**
         * Возвращает карту категорий разделов верхнего уровня.
         */
        public Map<Integer, List<CatalogItem>> getRootSectionCategoryMap() {
            return rootSectionCategoryMap;
        }

        protected void setRootSectionCategoryMap(Map<Integer, List<CatalogItem>> rootSectionCategoryMap) {
            this.rootSectionCategoryMap = rootSectionCategoryMap;
        }
    }

    /**
     * Создает экземпляр <code>RootSectionRetriever</code> по списку категорий.
     * @param categoryList список категорий.
     * @return
     */
    public RootSectionRetriever buildRootSectionRetrieverByCategoryList(List<CatalogItem> categoryList) {
        Set<Integer> categoryIdSet = new HashSet<Integer>();

        for (CatalogItem item : categoryList) {
            categoryIdSet.add(item.getId());
        }

        Map<Integer, List<CatalogItem>> categoryPathMap = catalogService.getCategoryPathMap(categoryIdSet);

        Map<Integer, List<CatalogItem>> rootSectionCategoryMap = new HashMap<Integer, List<CatalogItem>>();
        List<CatalogItem> rootSectionList = new ArrayList<CatalogItem>();

        for (Integer categoryId : categoryPathMap.keySet()) {
            List<CatalogItem> path = categoryPathMap.get(categoryId);
            CatalogItem rootSection = path.get(0);
            CatalogItem category = path.get(path.size() - 1);

            if (rootSectionCategoryMap.containsKey(rootSection.getId())) {
                rootSectionCategoryMap.get(rootSection.getId()).add(category);
            } else {
                List<CatalogItem> list = new ArrayList<CatalogItem>();
                list.add(category);
                rootSectionCategoryMap.put(rootSection.getId(), list);
                rootSectionList.add(rootSection);
            }
        }

        // TODO другая сортировка?
        for (Integer rootSectionId : rootSectionCategoryMap.keySet()) {
            Collections.sort(rootSectionCategoryMap.get(rootSectionId), new CatalogItemNameComparator());
        }
        Collections.sort(rootSectionList, new CatalogItemNameComparator());

        RootSectionRetriever rootSectionRetriever = new RootSectionRetriever();
        rootSectionRetriever.setRootSectionList(rootSectionList);
        rootSectionRetriever.setRootSectionCategoryMap(rootSectionCategoryMap);

        return rootSectionRetriever;
    }

    public Map<Integer, Unit> buildMatchCategoryUnitMap(Collection<AbstractMatch> matchList) {
        Assert.notNull(matchList);
        Set<Integer> categoryIdSet = buildMatchCategoryIdSet(matchList);
        return unitService.getCategoryUnitMap(categoryIdSet);
    }

    private Set<Integer> buildMatchCategoryIdSet(Collection<AbstractMatch> matchList) {
        Set<Integer> categoryIdSet = new HashSet<Integer>(matchList.size());
        for (AbstractMatch m : matchList) {
            switch (m.getType()) {
                case OFFER:
                    categoryIdSet.add(((OfferMatch) m).getOffer().getCategoryId());
                    break;
                case MODEL:
                    categoryIdSet.add(((ModelMatch) m).getModel().getCategoryId());
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип соответствия: " + m.getType());
            }
        }
        return categoryIdSet;
    }
}
