package ru.nullpointer.storefront.domain.support;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
public class PaginatedQueryResult<T> implements Iterable<T> {

    private PageConfig pageConfig;
    private List<T> list;
    private int total;

    /**
     * Результат постраничной выборки. Нумерация страниц с ЕДИНИЦЫ.
     * @param pageConfig настройки постраничной выборки
     * @param list Список результатов на странице. Может быть пустым
     * @param total Общее количество результатов на всех страницах. Может равняться нулю
     */
    public PaginatedQueryResult(PageConfig pageConfig, List<T> list, int total) {
        Assert.notNull(pageConfig);
        Assert.notNull(list);
        Assert.isTrue(total >= 0);

        // Номер страницы должен проверяться клиентом
        // Assert.isTrue((pageNumber - 1) * pageSize <= total);

        this.pageConfig = pageConfig;
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public int getTotal() {
        return total;
    }

    public int getPageNumber() {
        return pageConfig.getPageNumber();
    }

    public int getPageSize() {
        return pageConfig.getPageSize();
    }

    public int getPageCount() {
        int pageSize = pageConfig.getPageSize();
        return ((total / pageSize) + (total % pageSize > 0 ? 1 : 0));
    }

    public int getFirstNumber() {
        return (getPageNumber() - 1) * getPageSize() + 1;
    }

    public int getLastNumber() {
        return (getPageNumber() - 1) * getPageSize() + list.size();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("pageConfig", pageConfig)//
                .append("pageCount", getPageCount())//
                .append("list.size", list.size())//
                .append("total", total)//
                .toString();
    }
}
