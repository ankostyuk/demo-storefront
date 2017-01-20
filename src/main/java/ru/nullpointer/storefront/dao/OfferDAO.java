package ru.nullpointer.storefront.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.support.CompanyOfferShowing;
import ru.nullpointer.storefront.domain.support.CompanyOfferSorting;
import ru.nullpointer.storefront.domain.support.DateWindowConfig;
import ru.nullpointer.storefront.domain.support.NumberInterval;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.StringCount;

/**
 *
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public interface OfferDAO {

    /**
     * Возвращает количество товарных предложений в категории
     * @param categoryId идентификационный номер категории
     * @return количество товарных предложений в категории
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     */
    int getCategoryOfferCount(Integer categoryId);

    /**
     * Возвращает количество доступных товарных предложений в категории
     * Доступно если:
     * активно,
     * прошло модерацию,
     * ...
     * @param categoryId идентификационный номер категории
     * @return количество товарных предложений в категории
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     */
    int getCategoryAccessibleOfferCount(Integer categoryId);

    /**
     * Возвращает количество доступных товарных предложений связанных с моделью в категории
     * Доступно если:
     * активно,
     * прошло модерацию,
     * ...
     * @param categoryId идентификационный номер категории
     * @return количество товарных предложений связанных с моделью в категории
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     */
    int getCategoryAccessibleModelOfferCount(Integer categoryId);

    /**
     * Возвращает список товарных предложений категории
     * @param categoryId идентификационный номер категории
     * @param pageConfig настройки постраничного вывода
     * @return список товарных предложений категории или пустой список, если категория не имеет товарных предложений
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     */
    List<Offer> getCategoryOfferPaginatedList(Integer categoryId, PageConfig pageConfig);

    /**
     * Возвращает диапазон цен за единицу измерения на активные предложения во всем каталоге
     * @return
     */
    NumberInterval getCatalogOfferPriceInterval();

    /**
     * Возвращает диапазон цен за единицу измерения на активные предложения в категории
     * @param categoryId идентификатор категории
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     * @return
     */
    NumberInterval getCategoryOfferPriceInterval(Integer categoryId);

    /**
     * Возвращает диапазон цен за единицу измерения на активные предложения модели.
     * @param modelId идентификатор модели
     * @throws IllegalArgumentException если <code>modelId</code> равен <code>null</code>
     * @return
     */
    NumberInterval getModelOfferPriceInterval(Integer modelId);

    /**
     * Возвращает список товарных предложений поставщика
     * @param categoryIdSet набор идентификационных номеров категории или <code>null</code> для получения списка всех товарных предложений компании
     * @param pageConfig настройки постраничного вывода
     * @param showing настройки показа предложений
     * @param sorting параметр сортировки
     * @return
     * @throws IllegalArgumentException если любой из аргументов (кроме categoryIdSet) равен <code>null<code>
     */
    List<Offer> getCompanyOfferList(Integer companyId, Set<Integer> categoryIdSet, PageConfig pageConfig, CompanyOfferShowing showing, CompanyOfferSorting sorting);

    /**
     * Возвращает общее количество товарных предложений поставщика в категориях
     * @param companyId Идентификационный номер поставщика
     * @param categoryIdSet набор идентификационных номеров категорий
     * или <code>null</code> для получения общего числа всех товарных предложений поставщика
     * @param showing настройки показа предложений
     * @return
     * @throws IllegalArgumentException если любой из аргументов (кроме categoryIdSet) равен <code>null<code>
     */
    int getCompanyOfferCount(Integer companyId, Set<Integer> categoryIdSet, CompanyOfferShowing showing);

    /**
     * Возвращает карту количества товарных предложений поставщика
     * Ключем карты является идентификационный номер категории,
     * значением - количество товарных предложений поставщика в этой категории
     * @param companyId Идентификационный номер поставщика
     * @return
     * @throws IllegalArgumentException если <code>companyId</code> равен <code>null</code>
     */
    Map<Integer, Integer> getCompanyOfferCountMap(Integer companyId);

    /**
     * Возвращает карту количества товарных предложений ожидающих проверки
     * модератором
     * Ключем карты является идентификационный номер категории,
     * значением - количество товарных предложений в этой категории
     * @param moderatorId Идентификационный номер модератора
     * @return
     * @throws IllegalArgumentException если <code>moderatorId</code> равен <code>null</code>
     */
    Map<Integer, Integer> getModeratorPendingOfferCountMap(Integer moderatorId);

    /**
     * Возвращает список товарных предложений назначенных модератору
     * @param moderatorId ИД модератора
     * @param categoryId ИД категории
     * @param windowConfig настройки оконного вывода
     * @param pendingOnly если <code>true</code>, то вернуть только предложения ожидающие модерации
     * @return
     */
    List<Offer> getModeratorOfferList(Integer moderatorId, Integer categoryId, DateWindowConfig windowConfig, boolean pendingOnly);

    /**
     * Возвращает товарное предложение по <code>id</code>
     * @param id идентификационный номер товарного предложения
     * @return предложение или <code>null</code> если предложения с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    Offer getOfferById(Integer id);

    /**
     * Возвращает карту предложений.
     * @param offerIdSet набор <code>id</code> предложений
     * @return Карту предложений.
     * Ключем является <code>id</code> предложения.
     * Карта может быть пустой если список <code>offerIdSet</code> пуст или содержит неверные данные
     * @throws IllegalArgumentException если <code>offerIdSet</code> равен <code>null</code>
     */
    Map<Integer, Offer> getOfferMap(Set<Integer> offerIdSet);

    /**
     * Возвращает список имен брендов и кол-во товарных предложений не связанных со справочником брендов
     * @return
     */
    List<StringCount> getUnlinkedBrandNameList();

    /**
     * Устанавливает связь со справочником брендов для всех предложений 
     * с именем бренда <code>brandName</code> и еще не связанных со справочником брендов
     * @param brandName
     * @param brandId
     * @return количество измененных предложений
     */
    int setBrandByName(String brandName, Integer brandId);

    /**
     * Сохраняет товарное предложение. Устанавливает предложению полученный <code>id</code>
     * @param offer
     * @throws IllegalArgumentException если <code>offer</code> равен <code>null</code>
     */
    void insert(Offer offer);

    /**
     * Обновляет информацию о товарном предложении. 
     * Не меняет <code>id</code>, <code>categoryId</code>, <code>companyId</code>,
     * @param offer
     * @throws IllegalArgumentException если <code>offer</code> равен <code>null</code> 
     * или <code>id</code> предложения равен <code>null<code>
     */
    void updateInfo(Offer offer);

    /**
     * Удаляет товарное предложение
     * @param offer
     * @throws IllegalArgumentException если <code>offer</code> равен <code>null</code> 
     * или <code>id</code> предложения равен <code>null<code>
     */
    void delete(Offer offer);

    /**
     * Обновляет все цены за единицу измерения предложения.
     * Обновляеются только те предложения цены которых указаны в валюте <code>priceCurrency</code>.
     * @param defaultCurrency Основная валюта
     * @param priceCurrency Валюта цены предложения
     * @param companyId Идентификационный номер поставщика. Может равняться <code>null</code> для обновления цен всех поставщиков
     * @param offerId Идентификационный номер предложения. Если не <code>null</code> то обновляется цена только одного конкретного предложения
     * @return количество измененных предложений
     * @throws IllegalArgumentException если <code>defaultCurrency</code> или <code>priceCurrency</code> равен <code>null</code>
     */
    int updateOffersUnitPrice(String defaultCurrency, String priceCurrency, Integer companyId, Integer offerId);

    /**
     * Удаляет ссылки на дополнительные параметры у всех предложений категории
     * @param categoryId
     * @throws IllegalArgumentException если <code>categoryId</code>
     * @return количество измененных предложений
     */
    int removeParamSetRefs(Integer categoryId);

    /**
     * Устанавливает для всех предложений с <code>moderatorId</code> равным
     * <code>currentId</code> значение <code>moderatorId</code> выбранное
     * равномерно из списка <code>idSet</code>. Если список <code>idSet</code>
     * пуст - устанавливает значение в <code>null</code>
     * @param currentId ИД модератора или <code>null</code>
     *          для предложений не принадлежащих ни одному модератору
     * @param idSet список идентификационных номеров модераторов
     * @return количество измененных предложений
     */
    int setModeratorRefs(Integer currentId, Set<Integer> idSet);

    /**
     * Деактивирует все предложения у которых дата актуальности раньше <code>actualDate</code>
     * @param actualDate
     * @return количество деактивированных предложений
     */
    int deactivateOffers(Date actualDate);
}
