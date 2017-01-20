package ru.nullpointer.storefront.dao;

import java.util.List;
import ru.nullpointer.storefront.domain.CatalogItem;

/**
 *
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public interface CatalogItemDAO {

    /**
     * Возвращает значение уровня раздела или категории каталога. На основе "path" - не выполняет запрос к БД.
     * @param item раздел или категория
     * @return Значение уровня, 1 - значение верхнего уровня
     * @throws IllegalArgumentException если <code>item</code> равен <code>null</code> или если уровень(путь) не допустим
     */
    public int getItemLevel(CatalogItem item);
    //public int getItemLevel(Integer itemId); // TODO ? strong method - with SQL

    /**
     * Возвращает отсортированный список подразделов и категорий раздела.
     * @param sectionId идентификатор раздела или <code>null</code> для получения списка разделов верхнего уровня
     * @param onlyActive если <code>true<code> вернет только активные разделы и категории
     * @return Список разделов и категорий или пустой список если раздел с <code>sectionId</code> не существует или не содержит подразделов и категорий
     */
    public List<CatalogItem> getChildrenList(Integer sectionId, boolean onlyActive);

    /**
     * Возвращает отсортированный список подразделов и категорий раздела всех уровней вложенности.
     * @param sectionId идентификатор раздела или <code>null</code> для получения списка всех разделов и категорий каталога
     * @return Список разделов и категорий или пустой список если раздел с <code>sectionId</code> не существует или не содержит подразделов и категорий
     */
    public List<CatalogItem> getSubTree(Integer sectionId);

    /**
     * Возвращает родительский элемент раздела или категории каталога.
     * @param itemId идентификатор раздела или категории
     * @return элемент каталога или <code>null</code> если родительский элемент - корень каталога
     * @throws IllegalArgumentException если <code>itemId</code> равен <code>null</code> или если раздела или категории с таким <code>itemId</code> не существует
     */
    public CatalogItem getParent(Integer itemId);

    /**
     * Добавляет раздел или категорию в раздел (последним в списке дочерних). Устанавливает разделу(категории) полученный <code>id</code>.
     * @param item добавляемый раздел(категория)
     * @param sectionId идентификатор родительского раздела или <code>null</code> для корня каталога
     * @throws CatalogItemException нарушение порядка в списке дочерних родительского раздела
     * @throws IllegalArgumentException если <code>item</code> равен <code>null</code> или если будет нарушена субординация в иерархии каталога
     */
    public void addItem(CatalogItem item, Integer sectionId) throws CatalogItemException;

    /**
     * Удаляет раздел или категорию из каталога.
     * @param itemId идентификатор раздела или категории
     * @throws CatalogItemException
     * @throws IllegalArgumentException если <code>itemId</code> равен <code>null</code> или если будет нарушена субординация в иерархии каталога
     */
    public void deleteItem(Integer itemId) throws CatalogItemException;

    /**
     * Обновляет информацию об элементе каталога. Не обновляет идентификатор, тип, расположение, активность.
     * @param item
     * @throws IllegalArgumentException если <code>item</code> равен <code>null</code> или <code>id</code> элемента равен <code>null<code>
     */
    public void updateItemInfo(CatalogItem item);

    /**
     * Обновляет активнось элемента каталога.
     * @param item
     * @throws IllegalArgumentException если <code>item</code> равен <code>null</code> или <code>id</code> элемента равен <code>null<code>
     */
    public void updateItemActive(CatalogItem item);

    /**
     * Обновляет активнось раздела каталога и выставляет значение активности всем подэлементам раздела всех уровней вложенности.
     * @param sectionItem раздел каталога
     * @throws IllegalArgumentException если <code>sectionItem</code> равен <code>null</code> или <code>id</code> элемента равен <code>null<code>
     */
    public void updateSectionTreeActive(CatalogItem sectionItem);

    /**
     * Переносит раздел или категорию в новое место иерархии каталога, изменяет порядок и(или) родительский раздел.
     * @param itemId идентификатор раздела или категории
     * @param afterId идентификатор раздела или категории, перед которым(которой) будет новое место, или <code>null</code> для указания места как последнего в списке дочерних для <code>sectionId</code>, если указан <code>sectionId</code>, то параметр <code>afterId</code> должен быть <code>null</code>
     * @param sectionId идентификатор родительского раздела или <code>null</code> для корня каталога, если указан <code>afterId</code>, то параметр <code>sectionId</code> должен быть <code>null</code>
     * @throws CatalogItemException - нарушение порядка в списке дочерних родительского раздела
     * @throws IllegalArgumentException если <code>itemId</code> равен <code>null</code> или если будет нарушена субординация в иерархии каталога, или если указаны и <code>afterId</code>, и <code>sectionId</code>
     */
    public void moveItem(Integer itemId, Integer afterId, Integer sectionId) throws CatalogItemException;

    /**
     * Возвращает список активных популярных категорий раздела
     * @param sectionId идентификационный номер раздела
     * @return Список популярных категорий или пустой список если раздела с таким <code>sectionId</code> не существует
     * @throws IllegalArgumentException если <code>sectionId</code> равен <code>null</code>
     */
    public List<CatalogItem> getPopularCategories(Integer sectionId);

    /**
     * Возвращает элемент каталога
     * @param id идентификатор элемента каталога
     * @return элемент или <code>null</code> если элемента с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    public CatalogItem getItemById(Integer id);

    /**
     * Возвращает элемент соответствующий разделу каталога
     * @param id
     * @return элемент или <code>null</code> если элемента с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    public CatalogItem getSectionItemById(Integer id);

    /**
     * Возвращает элемент соответствующий категории каталога
     * @param id
     * @return элемент или <code>null</code> если элемента с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    public CatalogItem getCategoryItemById(Integer id);

    /**
     * Возвращает список элементов составляющих путь от корня каталога к разделу или категории.
     * Последним элементом списка является раздел или категория с указанным <code>id</code>
     * @param id идентификационный номер раздела или категории
     * @return список элементов
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    public List<CatalogItem> getPath(Integer id);

    /**
     * Возвращает количество разделов и категорий каталога
     */
    public int getActiveCatalogItemCount();

    /**
     * Возвращает список активных категорий,
     * в которых имеются доступные предложения связанные с брендом.
     * Сортировка по порядку и вложенности.
     * @param brandId идентификатор бренда
     * @return Список категорий или пустой список если категории отсутствуют
     */
    List<CatalogItem> getBrandActiveCategoryList(Integer brandId);

    /**
     * Возвращает список активных категорий,
     * в которых имеются доступные предложения Поставщика.
     * Сортировка по порядку и вложенности.
     * @param companyId идентификатор поставщика
     * @return Список категорий или пустой список если категории отсутствуют
     */
    List<CatalogItem> getCompanyActiveCategoryList(Integer companyId);

    /**
     * Проверяет является ли элемент каталога дочерним на любом уровне относительно другого элемента каталога.
     * @param item проверяемый элемент каталога
     * @param parentItem предполагаемый родительский элемент каталога
     * @throws IllegalArgumentException если любой из аргументов равен <code>null</code>
     */
    public boolean isChildItem(CatalogItem item, CatalogItem parentItem);

    // TODO to CatalogItem.equals
    public boolean isEquals(CatalogItem item, CatalogItem otherItem);
}
