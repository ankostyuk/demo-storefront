package ru.nullpointer.storefront.service.search;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 * @author ankostyuk
 */
public class SearchConfig {

    private int resultCount;
    private PageConfig pageConfig;
    private Sort sort;
    private Filter filter;

    /**
     * Настройки поиска.
     *
     * @param resultCount
     *            максимальное количество найденных документов, может иметь любое положительное значение
     * @param pageConfig
     *            настройки постраничного вывода, может равняться <code>null</code>
     * @param sort
     *            сортировка
     * @param filter
     *            фильтр, может равняться <code>null</code>
     * @throws IllegalArgumentException
     *             <code>resultCount</code> меньше <code>0</code>,
     *             <code>sort</code> равен <code>null</code>
     */
    public SearchConfig(int resultCount, PageConfig pageConfig, Sort sort, Filter filter) {
        Assert.isTrue(resultCount >= 0);
        Assert.notNull(sort);

        this.resultCount = resultCount;
        this.pageConfig = pageConfig;
        this.sort = sort;
        this.filter = filter;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public PageConfig getPageConfig() {
        return pageConfig;
    }

    public void setPageConfig(PageConfig pageConfig) {
        this.pageConfig = pageConfig;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
