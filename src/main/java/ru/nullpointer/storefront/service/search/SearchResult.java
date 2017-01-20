package ru.nullpointer.storefront.service.search;

import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.springframework.util.Assert;

/**
 * @author ankostyuk
 */
public class SearchResult<T> implements Iterable<T> {

    private List<T> list;
    private int total;
    private Query query;
    private Analyzer analyzer;

    /**
     * Результат поиска.
     * @param list Список результатов. Может быть пустым.
     */
    public SearchResult(List<T> list, int total, Query query, Analyzer analyzer) {
        Assert.notNull(list);
        Assert.isTrue(total >= 0);
        
        this.list = list;
        this.total = total;
        this.query = query;
        this.analyzer = analyzer;
    }

    public List<T> getList() {
        return list;
    }

    public int getTotal() {
        return total;
    }

    public Query getQuery() {
        return query;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("list.size", list.size())//
                .append("total", total)//
                .toString();
    }
}
