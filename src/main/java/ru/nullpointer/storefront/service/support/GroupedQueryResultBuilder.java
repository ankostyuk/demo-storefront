package ru.nullpointer.storefront.service.support;

import java.util.List;
import ru.nullpointer.storefront.domain.support.ResultGroup;

/**
 *
 * @author Alexander Yastrebov
 */
public interface GroupedQueryResultBuilder<T> {

    String getGroupValue(T obj);
    
    List<String> buildToc(List<String> list);
    
    List<ResultGroup<T>> buildResult(List<T> list);
}
