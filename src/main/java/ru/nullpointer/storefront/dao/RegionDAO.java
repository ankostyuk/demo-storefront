package ru.nullpointer.storefront.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public interface RegionDAO {

    /**
     * Возвращает регион по идентификационному номеру
     * @param id
     * @return регион или <code>null</code> если региона с таким номером не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    Region getRegionById(Integer id);

    /**
     * Возвращает список регионов по вхождению текста в наименовании.
     * Вхождение текста определяется в любом месте наименования и без учета регистра.
     * @param text текст в наименовании региона
     * @param pageConfig настройки постраничного вывода
     * @return список регионов (сортировка: в порядке "крупности" + в алфавитном порядке), или пустой список, если отсутствует вхождение текста в наименовании регионов.
     * @throws IllegalArgumentException если <code>text</code> равен <code>null</code>
     */
    List<Region> getRegionsByNameText(String text);

    /**
     * Постранично возвращает список регионов по вхождению текста в наименовании.
     * Вхождение текста определяется в любом месте наименования и без учета регистра.
     * @param text текст в наименовании региона
     * @param pageConfig настройки постраничного вывода
     * @return список регионов (сортировка: в порядке "крупности" + в алфавитном порядке), или пустой список, если отсутствует вхождение текста в наименовании регионов.
     * @throws IllegalArgumentException если <code>text</code> равен <code>null</code>
     */
    List<Region> getRegionsPaginatedByNameText(String text, PageConfig pageConfig);

    /**
     * Возвращает количество регионов по вхождению текста в наименовании.
     * @param text текст в наименовании региона
     * @return количество регионов.
     * @throws IllegalArgumentException если <code>text</code> равен <code>null</code>
     */
    int getRegionsByNameTextCount(String text);

    /**
     * Возвращает путь региона как список родительских регионов.
     * @param region регион
     * @return список родительских регионов для региона, отсортированный в порядке вложенности (первый элемент в списке - регион 1-го уровня), 
     * или пустой список, если регион 1-го уровня.
     * @throws IllegalArgumentException если <code>region</code> равен <code>null</code> или обязательные атрибуты не "валидны"
     */
    List<Region> getRegionPath(Region region);

    /**
     * Возвращает карту регионов. Ключем карты является идентификатор региона, значением - регион.
     * Карта может быть пустой если список <code>regionIdSet</code> пуст или содержит неверные данные
     * @param regionIdSet набор идентификаторов регионов
     * @return
     * @throws IllegalArgumentException если <code>regionIdSet</code> равен <code>null</code>
     */
    Map<Integer, Region> getRegionMap(Set<Integer> regionIdSet);

    /**
     * Возвращает список регионов доставки поставщика
     * @param companyId ИД поставщика
     * @return список регионов или пустой список если поставщик не указал ни одного региона доставки
     * или поставщика с таким ИД не существует
     * @throws IllegalArgumentException если <code>companyId</code> равен <code>null</code>
     */
    List<Region> getCompanyDeliveryRegionList(Integer companyId);

    /**
     * Добавляет регион доставки поставщика
     * @param companyId
     * @param regionId
     * @throws IllegalArgumentException если любой аргумент равен <code>null</code>
     */
    void insertCompanyDeliveryRegion(Integer companyId, Integer regionId);

    /**
     * Удаляет регион доставки поставщика
     * @param companyId
     * @param regionId
     * @throws IllegalArgumentException если любой аргумент равен <code>null</code>
     */
    void deleteCompanyDeliveryRegion(Integer companyId, Integer regionId);
}
