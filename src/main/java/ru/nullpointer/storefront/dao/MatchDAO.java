package ru.nullpointer.storefront.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.ModelInfo;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.param.ParamSetDescriptor;
import ru.nullpointer.storefront.domain.support.MatchFilter;
import ru.nullpointer.storefront.domain.support.MatchSorting;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public interface MatchDAO {

    /**
     * Возвращает список соответствий (предложений и моделей) в категории.
     * @param categoryId идентификационный номер категории
     * @param pageConfig настройки постраничного вывода
     * @param sorting параметр сортировки
     * @param region регион, может быть <code>null</code>
     * @param filter фильтр по параметрам, может быть <code>null</code>
     * @param psd дескриптор набора параметров, может быть <code>null</code>
     * @return
     * @throws IllegalArgumentException если любой из обязательных аргументов равен <code>null<code>
     */
    List<Match> getMatchList(Integer categoryId, PageConfig pageConfig, MatchSorting sorting, Region region, MatchFilter filter, ParamSetDescriptor psd);

    /**
     * Возвращает количество соответсвий (моделей и предложений)
     * @param categoryId идентификационный номер категории
     * @param region регион, может быть <code>null</code>
     * @param filter фильтр по параметрам, может быть <code>null</code>
     * @param psd дескриптор набора параметров, может быть <code>null</code>
     * @return
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     */
    int getMatchCount(Integer categoryId, Region region, MatchFilter filter, ParamSetDescriptor psd);

    /**
     * Возвращает карту дополнительной информации о модели.
     * Ключем карты является <code>id</code> модели.
     * Значением по ключу карты является дополнительная информация о модели,
     * при условии наличия у модели доступных товарных предложений, иначе <code>null</code>.
     * @param modelIdSet набор идентификационных номеров моделей
     * @param region регион, может быть <code>null</code>
     * @return
     */
    Map<Integer, ModelInfo> getModelInfoMap(Set<Integer> modelIdSet, Region region);

    /**
     * Возвращает список идентификаторов <b>доступных</b> товарных предложений модели.
     * @param modelId идентификатор модели
     * @param pageConfig настройки постраничного вывода
     * @param sorting параметр сортировки
     * @param region регион, может быть <code>null</code>
     * @param filter фильтр по <b>цене</b>, может быть <code>null</code>
     * @return
     * @throws IllegalArgumentException если любой из обязательных аргументов равен <code>null<code>
     */
    List<Integer> getModelOfferIdList(Integer modelId, PageConfig pageConfig, MatchSorting sorting, Region region, MatchFilter filter);

    /**
     * Возвращает количество <b>доступных</b> товарных предложений модели.
     * @param modelId идентификатор модели
     * @param region регион, может быть <code>null</code>
     * @param filter фильтр по <b>цене</b>, может быть <code>null</code>
     * @return
     * @throws IllegalArgumentException если любой из обязательных аргументов равен <code>null<code>
     */
    int getModelOfferCount(Integer modelId, Region region, MatchFilter filter);

    /**
     * Возвращает доступное подмножество совпадений.
     * Совпадение товарного предложения является доступным если:
     *      - предложение существует в каталоге
     *      - предложение является активным и одобрено модератором
     *      - если <code>categoryId</code> не равен <code>null</code>, то
     *          предложение должно принадлежать заданной категории
     *      - если <code>parametrizedOnly<code> равен <code>true</code>, то
     *          предложение должно быть с параметрами или связанным с моделью
     * Совпадение модели является доступным если:
     *      - модель существует в каталоге
     *      - если <code>categoryId</code> не равен <code>null</code>, то
     *          модель должна принадлежать заданной категории
     * @param matchSet множество совпадений
     * @param categoryId категория совпадения или <code>null</code>
     * @param parametrizedOnly если <code>true</code> включить только параметризованные предложения
     *          или предложения связанные с моделью
     * @return
     * @throws IllegalArgumentException если <code>matchSet</code> равен <code>null</code>
     */
    Set<Match> getAccessibleMatchSubset(Set<Match> matchSet, Integer categoryId, boolean parametrizedOnly);
}
