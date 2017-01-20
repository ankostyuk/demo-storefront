package ru.nullpointer.storefront.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.nullpointer.storefront.domain.Category;

/**
 *
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public interface CategoryDAO {

    /**
     * Возвращает категорию каталога по идентификационному номеру
     * @param categoryId
     * @return Категорию или <code>null</code> если элемента каталога с таким номером не существует
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code> 
     */
    Category getCategoryById(Integer categoryId);
    
    /**
     * Возвращает карту категорий. 
     * @param categoryIdSet набор <code>id</code> категорий
     * @return Карту категорий. Ключем является <code>id</code> категории. Карта может быть пустой если список <code>categoryIdSet</code> пуст или содержит неверные данные
     * @throws IllegalArgumentException если <code>categoryIdSet</code> равен <code>null</code>
     */
    Map<Integer, Category> getCategoryMap(Set<Integer> categoryIdSet);
    
    /**
     * Возвращает список всех категорий, отсортированный по <code>id</code>.
     * @return Список категорий или пустой список если категории отсутствуют
     */
    List<Category> getAllCategories();
    
    /**
     * Добавляет категорию. <code>id</code> - дожен совпадать со связанным <code>CatalogItem.id</code>, являющимся категорией.
     * @param category добавляемая категория
     * @throws IllegalArgumentException если <code>category</code> равен <code>null</code> или обязательные атрибуты равны <code>null</code>
     * @throws TODO Exception если нарушена связь с <code>CatalogItem</code>
     */
    void addCategory(Category category);
    
    /**
     * Обновляет информацию о категории.
     * @param category обновляемая категория
     * @throws IllegalArgumentException если <code>category</code> равен <code>null</code> или обязательные атрибуты равны <code>null</code>
     */
    void updateCategoryInfo(Category category);
    
    /**
     * Удаляет категорию.
     * @param categoryId идентификатор категории
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     * @throws TODO Exception если нарушена связь со связанными данными
     */
    void deleteCategory(Integer categoryId);
    
    /**
     * Обновляет связь категории с параметрами.
     * @param category категория
     * @throws IllegalArgumentException если <code>category</code> равен <code>null</code> или обязательные атрибуты равны <code>null</code>
     * @throws TODO Exception если нарушена связь со связанными данными
     */
    void updateCategoryParameterSetDescriptor(Category category);
    
    /**
     * Возвращает категорию каталога, связанную с конкретным дескриптором набора параметров.
     * @param psdId идентификатор дескриптора набора параметров
     * @return Категория каталога или <code>null</code> если категорию каталога, связанная с конкретным дескриптором набора параметров, отсутствует
     * @throws IllegalArgumentException если <code>psdId</code> равен <code>null</code>
     */
    Category getCategoryByParamSetDescriptorId(Integer psdId);
}
