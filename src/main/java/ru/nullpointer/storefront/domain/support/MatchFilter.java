package ru.nullpointer.storefront.domain.support;

import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Alexander Yastrebov
 */
public class MatchFilter {

    private NumberInterval price;
    private Set<Integer> brandIdSet;
    private Map<Integer, Object> paramValueMap;

    public MatchFilter(NumberInterval price, Set<Integer> brandIdSet, Map<Integer, Object> paramValueMap) {
        this.price = price;
        this.brandIdSet = brandIdSet;
        this.paramValueMap = paramValueMap;
    }

    public NumberInterval getPrice() {
        return price;
    }

    public Set<Integer> getBrandIdSet() {
        return brandIdSet;
    }

    /**
     * Возвращает карту значений параметров фильтрации или <code>null</code>.
     * Ключем карты является идентификатор параметра. Значением является:
     * - для логического параметра: Boolean
     * - для числового параметра: NumberInterval - минимальное и максимальное значение. Любое из них может равняться <code>null</code>
     * - для выборочного параметра: List<Integer> - список идентификаторов вариантов выбора
     */
    public Map<Integer, Object> getParamValueMap() {
        return paramValueMap;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("price", price)//
                .append("brandIdSet", brandIdSet)//
                .append("paramValueMap", paramValueMap)//
                .toString();
    }
}
