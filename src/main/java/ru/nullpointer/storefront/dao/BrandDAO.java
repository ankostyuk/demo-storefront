package ru.nullpointer.storefront.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nullpointer.storefront.domain.Brand;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public interface BrandDAO {

    /**
     * Возвращает количество брендов
     */
    public int getBrandCount();

    /**
     * Возвращает список производителей, отсортированный в алфавитном порядке наименований.
     * @return 
     */
    List<Brand> getBrandList();

    /**
     * Возвращает список префиксов имен брендов.
     * @return
     */
    List<String> getBrandNamePrefixList();

    /**
     * Возвращает производителя по идентификатору
     * @param id идентификатор производителя
     * @return производителя или <code>null</code> если производителя с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    Brand getBrandById(Integer id);

    /**
     * Возвращает карту производителей.
     * @param brandIdSet набор <code>id</code> производителей
     * @return 
     * @throws IllegalArgumentException если <code>brandIdSet</code> равен <code>null</code>
     */
    Map<Integer, Brand> getBrandMap(Set<Integer> brandIdSet);

    /**
     * Возвращает список брендов по набору <code>id</code> брендов.
     * Сортировка в алфавитном порядке наименования бренда.
     * @param brandIdSet набор <code>id</code> брендов
     * @return 
     * @throws IllegalArgumentException если <code>brandIdSet</code> равен <code>null</code>
     */
    List<Brand> getBrandListByIdSet(Set<Integer> brandIdSet);

    /**
     * Возвращает список брендов по вхождению текста в наименование
     * и ключевые слова
     * @param text
     * @param limit максимальное количество возвращаемых брендов
     * @return
     */
    List<Brand> findBrandListByText(String text, int limit);

    /**
     * Возвращает список брендов, для которых существуют предложения в категории.
     * Список отсортирован в алфавитном порядке наименования бренда.
     * @param categoryId
     * @return
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     */
    List<Brand> getCategoryBrandList(Integer categoryId);

    /**
     * Возвращает список брендов из набора брендов, для которых существуют предложения в наборе категорий.
     * Сортировка в алфавитном порядке наименования бренда.
     * @param brandIdSet набор <code>id</code> брендов
     * @param categoryIdSet набор <code>id</code> категорий
     * @return
     * @throws IllegalArgumentException если <code>brandIdSet</code> равен <code>null</code> или
     * <code>categoryIdSet</code> равен <code>null</code>
     */
    List<Brand> getIntersectionList(Set<Integer> brandIdSet, Set<Integer> categoryIdSet);
    
    /**
     * Добавляет производителя. Устанавливает производителю полученный <code>id</code>.
     * @param brand производитель
     * @throws IllegalArgumentException если <code>brand</code> равен <code>null</code> 
     */
    void insert(Brand brand);

    /**
     * Обновляет информацию о производителе.
     * Не обновляет идентификатор.
     * @param brand производитель
     * @throws IllegalArgumentException если <code>brand</code> равен <code>null</code>
     * или <code>id</code> производителя равен <code>null</code>
     */
    void updateInfo(Brand brand);

    /**
     * Удаляет производителя.
     * @param id идентификатор производителя
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    void delete(Integer id);
}
