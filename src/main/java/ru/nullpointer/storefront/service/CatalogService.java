package ru.nullpointer.storefront.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CatalogItemDAO;
import ru.nullpointer.storefront.dao.CatalogItemException;
import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.dao.ParamSetDAO;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.service.search.SearchIndexing;
import ru.nullpointer.storefront.service.support.FieldUtils;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Service
public class CatalogService {

    private Logger logger = LoggerFactory.getLogger(CatalogService.class);
    //
    
    @Resource
    private CatalogItemDAO catalogItemDAO;
    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private ParamSetDAO psdDAO;

    /**
     * Возвращает значение уровня раздела или категории каталога.
     * @param item раздел или категория
     * @return Значение уровня, 1 - значение верхнего уровня
     * @throws IllegalArgumentException если <code>item</code> равен <code>null</code> или если уровень(путь) не допустим
     */
    //@Transactional(readOnly = true)
    public int getItemLevel(CatalogItem item) {
        return catalogItemDAO.getItemLevel(item);
    }

    /**
     * Возвращает отсортированный список подразделов и категорий раздела.
     * @param sectionId идентификатор раздела или <code>null</code> для получения списка разделов верхнего уровня
     * @return Список разделов и категорий или пустой список если раздел с <code>sectionId</code> не существует или не содержит подразделов и категорий
     */
    @Transactional(readOnly = true)
    public List<CatalogItem> getChildrenList(Integer sectionId) {
        return catalogItemDAO.getChildrenList(sectionId, false);
    }

    /**
     * Возвращает отсортированный список активных подразделов и активных категорий раздела.
     * @param sectionId идентификатор раздела или <code>null</code> для получения списка разделов верхнего уровня
     * @return Список разделов и категорий или пустой список если раздел с <code>sectionId</code> не существует или не содержит подразделов и категорий
     */
    @Transactional(readOnly = true)
    public List<CatalogItem> getActiveChildrenList(Integer sectionId) {
        return catalogItemDAO.getChildrenList(sectionId, true);
    }

    /**
     * Возвращает отсортированный список подразделов и категорий раздела всех уровней вложенности.
     * @param sectionId идентификатор раздела или <code>null</code> для получения списка всех разделов и категорий каталога
     * @return Список разделов и категорий или пустой список если раздел с <code>sectionId</code> не существует или не содержит подразделов и категорий
     */
    @Transactional(readOnly = true)
    public List<CatalogItem> getSubTree(Integer sectionId) {
        return catalogItemDAO.getSubTree(sectionId);
    }

    /**
     * Возвращает родительский элемент раздела или категории каталога.
     * @param itemId идентификационный номер раздела или категории
     * @return элемент каталога или <code>null</code> если родительский элемент - корень каталога
     * @throws IllegalArgumentException если <code>itemId</code> равен <code>null</code> или если раздела или категории с таким <code>itemId</code> не существует
     */
    @Transactional(readOnly = true)
    public CatalogItem getParentItem(Integer itemId) {
        return catalogItemDAO.getParent(itemId);
    }

    /**
     * Возвращает следующий элемент каталога в порядке дочерних родительского раздела.
     * @param itemId идентификационный номер раздела или категории
     * @return элемент каталога или <code>null</code> если следующий элемент отсутствует
     * @throws IllegalArgumentException если <code>item</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public CatalogItem getAfterItem(CatalogItem item) {
        Assert.notNull(item);
        Assert.notNull(item.getId());

        CatalogItem afterItem = null;

        CatalogItem parentItem = getParentItem(item.getId());

        List<CatalogItem> afterItems = getChildrenList(parentItem != null ? parentItem.getId() : null);
        for (int i = 0; i < afterItems.size(); i++) {
            CatalogItem after = afterItems.get(i);
            if (item.getId().equals(after.getId())) {
                afterItem = (i < afterItems.size() - 1 ? afterItems.get(i + 1) : null);
                break;
            }
        }

        return afterItem;
    }

    /**
     * Добавляет раздел в каталог (последним в списке дочерних родительского раздела <code>sectionId</code>).
     * Устанавливает разделу полученный <code>id</code>.
     * Делает раздел неактивным.
     * @param sectionItem добавляемый раздел
     * @param sectionId идентификатор родительского раздела или <code>null</code> для корня каталога
     * @throws IllegalArgumentException
     * @throws CatalogItemException
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    @SearchIndexing
    public void addSection(CatalogItem sectionItem, Integer sectionId) throws CatalogItemException {
        Assert.isTrue(isSectionItem(sectionItem));

        // force deactive item
        sectionItem.setActive(false);
        FieldUtils.nullifyIfEmpty(sectionItem, "theme");

        catalogItemDAO.addItem(sectionItem, sectionId);
    }

    /**
     * Добавляет категорию в каталог (последней в списке дочерних родительского раздела <code>sectionId</code>).
     * Устанавливает категории полученный <code>id</code>.
     * Делает категорию неактивной.
     * @param categoryItem добавляемая категория как элемент каталога
     * @param category добавляемая категория
     * @param sectionId идентификатор родительского раздела
     * @throws IllegalArgumentException если один из аргументов равен <code>null</code> или если необходимые атрибуты <code>categoryItem</code>, <code>category</code> равны <code>null</code>
     * @throws CatalogItemException нарушение порядка в списке дочерних родительского раздела
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    @SearchIndexing
    public void addCategory(CatalogItem categoryItem, Category category, Integer sectionId) throws CatalogItemException {
        Assert.notNull(categoryItem);
        Assert.notNull(category);
        Assert.notNull(sectionId);
        Assert.isTrue(isCategoryItem(categoryItem));

        // force deactive item
        categoryItem.setActive(false);
        FieldUtils.nullifyIfEmpty(categoryItem, "theme");

        catalogItemDAO.addItem(categoryItem, sectionId);
        category.setId(categoryItem.getId());
        categoryDAO.addCategory(category);
    }

    /**
     * Удаляет раздел из каталога.
     * @param sectionId идентификатор раздела
     * @throws CatalogItemException 
     * @throws IllegalArgumentException
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    @SearchIndexing
    public void deleteSection(Integer sectionId) throws CatalogItemException {
        CatalogItem sectionItem = catalogItemDAO.getSectionItemById(sectionId);
        Assert.notNull(sectionItem);
        Assert.isTrue(canDeleteItem(sectionItem));
        catalogItemDAO.deleteItem(sectionItem.getId());
    }

    /**
     * Удаляет категорию из каталога.
     * @param categoryItem категория каталога
     * @throws CatalogItemException нарушение субординации каталога
     * @throws IllegalArgumentException если <code>categoryItem</code> равен <code>null</code> или если <code>categoryItem</code> не является категорией
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    @SearchIndexing
    public void deleteCategory(CatalogItem categoryItem) throws CatalogItemException {
        Assert.isTrue(isCategoryItem(categoryItem));
        Assert.isTrue(canDeleteItem(categoryItem), "deleteCategory: Категория не может быть удалена");
        Assert.notNull(categoryItem.getId());
        Category category = categoryDAO.getCategoryById(categoryItem.getId());
        Assert.notNull(category);
        categoryDAO.deleteCategory(category.getId());
        if (category.getParameterSetDescriptorId() != null) {
            psdDAO.deleteParamSetDescriptor(category.getParameterSetDescriptorId());
        }
        catalogItemDAO.deleteItem(categoryItem.getId());
    }

    /**
     * Обновляет информацию о разделе каталога. Не обновляет идентификатор, тип, расположение.
     * Для обновления активности вызывает метод {@link updateSectionTreeActive(CatalogItem sectionItem)}.
     * @param sectionItem раздел каталога
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    @SearchIndexing
    public void updateSectionInfo(CatalogItem sectionItem) { // TODO проверка на наличие изменений?
        Assert.isTrue(isSectionItem(sectionItem));

        FieldUtils.nullifyIfEmpty(sectionItem, "theme");
        
        catalogItemDAO.updateItemInfo(sectionItem);
        if (canActivateItem(sectionItem)) {
            updateSectionTreeActive(sectionItem);
        }
    }

    /**
     * Обновляет активнось раздела каталога и выставляет значение активности всем подэлементам раздела всех уровней вложенности.
     * @param sectionItem раздел каталога
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    @SearchIndexing
    public void updateSectionTreeActive(CatalogItem sectionItem) { // TODO проверка на наличие изменений?
        Assert.isTrue(isSectionItem(sectionItem));
        Assert.isTrue(canActivateItem(sectionItem));
        catalogItemDAO.updateSectionTreeActive(sectionItem);
    }

    /**
     * Обновляет информацию о категории каталога. Не обновляет идентификатор, тип, расположение. Для обновления активности вызывает метод <code>updateCategoryActive(CatalogItem)</code>.
     * @param categoryItem категория как элемент каталога
     * @param category категория
     * @throws IllegalArgumentException если один из аргументов равен <code>null</code> или если <code>categoryItem</code> не является категорией
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    @SearchIndexing
    public void updateCategoryInfo(CatalogItem categoryItem, Category category) { // TODO проверка на наличие изменений?
        Assert.isTrue(isCategoryItem(categoryItem));
        Assert.isTrue(categoryItem.getId().equals(category.getId()));

        FieldUtils.nullifyIfEmpty(categoryItem, "theme");

        catalogItemDAO.updateItemInfo(categoryItem);
        if (canActivateItem(categoryItem)) {
            updateCategoryActive(categoryItem);
        }
        categoryDAO.updateCategoryInfo(category);
    }

    /**
     * Обновляет активнось категории каталога.
     * @param categoryItem категория каталога
     * @throws IllegalArgumentException если <code>categoryItem</code> равен <code>null</code>
     * или если <code>categoryItem</code> не является категорией,
     * или если активность категории не может быть изменена
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    @SearchIndexing
    public void updateCategoryActive(CatalogItem categoryItem) {
        Assert.isTrue(canActivateItem(categoryItem));
        catalogItemDAO.updateItemActive(categoryItem);
    }

    /**
     * Переносит элемент каталога в новое место иерархии каталога, изменяет родительский раздел, при измении родительского раздела располагает элемент каталога последним в списке дочерних. 
     * @param item элемент каталога
     * @param sectionId идентификатор родительского раздела или <code>null</code> для корня каталога
     * @throws CatalogItemException 
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    @SearchIndexing
    public void changeParentItem(CatalogItem item, Integer sectionId) throws CatalogItemException {
        Assert.notNull(item);

        CatalogItem parentItemOld = this.getParentItem(item.getId());
        Integer parentItemIdOld = (parentItemOld != null ? parentItemOld.getId() : null);

        if (sectionId != null ? !sectionId.equals(parentItemIdOld) : (parentItemIdOld != null) ? true : false) {
            //catalogItemDAO.moveItem(itemId, null, sectionId);
            moveItem(item, null, sectionId, true);
        } else {
            logger.info("### changeParentItem: nothing change");
        }
    }

    /**
     * Переносит элемент каталога в новое место иерархии каталога.
     * @param itemId идентификатор элемента каталога
     * @param afterId идентификатор элемента каталога, перед которым будет новое место, или <code>null</code> для указания места как последнего в списке дочерних родительского раздела
     * @throws CatalogItemException 
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void changeAfterItem(Integer itemId, Integer afterId) throws CatalogItemException {
        CatalogItem item = catalogItemDAO.getItemById(itemId);
        Assert.notNull(item);

        CatalogItem afterItemOld = getAfterItem(item);
        Integer afterItemIdOld = (afterItemOld != null ? afterItemOld.getId() : null);

        if (afterId != null) {
            if (!afterId.equals(afterItemIdOld)) {
                //catalogItemDAO.moveItem(itemId, afterId, null);
                moveItem(item, afterId, null, true);
            } else {
                logger.info("### changeAfterItem: nothing change");
            }
        } else {
            if ((afterItemIdOld != null) ? true : false) {
                CatalogItem parentItem = getParentItem(item.getId());
                Integer parentItemId = (parentItem != null ? parentItem.getId() : null);
                //catalogItemDAO.moveItem(itemId, null, parentItemId);
                moveItem(item, null, parentItemId, true);
            } else {
                logger.info("### changeAfterItem: nothing change");
            }
        }
    }

    /**
     * Сдвигает элемент каталога в списке дочерних родительского раздела на один порядок вверх или вниз.
     * @param itemId идентификатор элемента каталога
     * @param shiftUp если <code>true</code>, то сдвиг вверх, иначе - вниз
     * @throws CatalogItemException 
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void shiftItem(Integer itemId, boolean shiftUp) throws CatalogItemException {
        CatalogItem shiftItem = catalogItemDAO.getItemById(itemId);
        Assert.notNull(shiftItem);

        CatalogItem itemParent = catalogItemDAO.getParent(itemId);

        List<CatalogItem> parentChildren = catalogItemDAO.getChildrenList(itemParent != null ? itemParent.getId() : null, false);

        Integer afterItemId = null;
        Integer sectionItemId = null;
        int parentChildrenCount = parentChildren.size();
        for (int i = 0; i < parentChildrenCount; i++) {
            CatalogItem item = parentChildren.get(i);
            if (item.getId().equals(itemId)) {
                if (shiftUp) {
                    if (i == 0) {
                        logger.debug("### shiftItem - nothing shift");
                    }
                    afterItemId = (i > 0 ? parentChildren.get(i - 1).getId() : null);
                    sectionItemId = null;
                } else {
                    if (i == parentChildrenCount - 1) {
                        logger.debug("### shiftItem - nothing shift");
                    }
                    afterItemId = (i < parentChildrenCount - 2 ? parentChildren.get(i + 2).getId() : null);
                    sectionItemId = (i == parentChildrenCount - 2 ? (itemParent != null ? itemParent.getId() : null) : null);
                }
                break;
            }
        }

        moveItem(shiftItem, afterItemId, sectionItemId, false);
    }

    /**
     * Проверяет возможность удаления элемента каталога.
     * @param item элемент каталога
     * @return Возможность удаления - <code>true</code>, иначе - <code>false</code>
     * @throws IllegalArgumentException если <code>item</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public boolean canDeleteItem(CatalogItem item) {
        Assert.notNull(item);
        Assert.notNull(item.getId());

        if (isCategoryItem(item)) {
            int offerCount = offerDAO.getCategoryOfferCount(item.getId());
            return (offerCount < 1);
        } else if (isSectionItem(item)) {
            List<CatalogItem> childrenList = catalogItemDAO.getChildrenList(item.getId(), false);
            return (childrenList.isEmpty());
        }

        return false;
    }

    /**
     * Проверяет возможность изменения активности элемента каталога.
     * @param item элемент каталога
     * @return Возможность изменения активности - <code>true</code>, иначе - <code>false</code>
     * @throws IllegalArgumentException если <code>item</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public boolean canActivateItem(CatalogItem item) {
        Assert.notNull(item);
        Assert.notNull(item.getId());

        CatalogItem parentItem = catalogItemDAO.getParent(item.getId());

        if (parentItem == null) {
            // Корень каталога - возможно изменение активности
            return true;
        } else {
            // Если родительский активен - возможно изменение активности
            return parentItem.getActive();
        }
    }

    /**
     * Возвращает карту активных популярных подкатегорий.
     * Ключем карты является идентификатор раздела, значением - список популярных подкатегорий
     * @param sectionIdSet набор идентификаторов разделов, для которых необходимо вернуть карту
     * @return
     * @throws IllegalArgumentException если <code>sectionIdSet</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Map<Integer, List<CatalogItem>> getPopularCategoriesMap(Set<Integer> sectionIdSet) {
        Assert.notNull(sectionIdSet);

        Map<Integer, List<CatalogItem>> result = new HashMap<Integer, List<CatalogItem>>();
        for (Integer id : sectionIdSet) {
            List<CatalogItem> popular = catalogItemDAO.getPopularCategories(id);
            if (!popular.isEmpty()) {
                result.put(id, popular);
            }
        }
        return result;
    }

    /**
     * Возвращает элемент каталога
     * @param id
     * @return элемент или <code>null</code> если элемента с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public CatalogItem getItemById(Integer id) {
        return catalogItemDAO.getItemById(id);
    }

    /**
     * Возвращает элемент соответствующий разделу каталога
     * @param id
     * @return элемент или <code>null</code> если элемента с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public CatalogItem getSectionItemById(Integer id) {
        return catalogItemDAO.getSectionItemById(id);
    }

    /**
     * Возвращает элемент соответствующий категории каталога
     * @param id
     * @return элемент или <code>null</code> если элемента с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public CatalogItem getCategoryItemById(Integer id) {
        return catalogItemDAO.getCategoryItemById(id);
    }

    /**
     * Возвращает список элементов составляющих путь от корня каталога к разделу или категории.
     * Последним элементом списка является раздел или категория с указанным <code>id</code>
     * @param id идентификационный номер раздела или категории
     * @return список элементов
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public List<CatalogItem> getPath(Integer id) {
        return catalogItemDAO.getPath(id);
    }

    /**
     * Возвращает карту путей категорий.
     * @param categoryIdSet набор <code>id</code> категорий
     * @return Карту путей категорий.
     * Ключем является <code>id</code> категории.
     * Значением является список элементов каталога, составляющих путь от корня каталога к категории,
     * последним элементом списка является категория с указанным <code>id</code>.
     * Карта может быть пустой если список <code>categoryIdSet</code> пуст или содержит неверные данные
     * @throws IllegalArgumentException если <code>categoryIdSet</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Map<Integer, List<CatalogItem>> getCategoryPathMap(Set<Integer> categoryIdSet) {
        Assert.notNull(categoryIdSet);
        // TODO: Подумать как оптимизировать получение карты путей без цикла
        Map<Integer, List<CatalogItem>> map = new HashMap<Integer, List<CatalogItem>>(categoryIdSet.size());
        for (Integer id : categoryIdSet) {
            map.put(id, catalogItemDAO.getPath(id));
        }
        return map;
    }

    /**
     * Возвращает объединенный список путей элементов каталога.
     * @param itemIdSet набор <code>id</code> элементов каталога
     * @return Список путей элементов каталога.
     * Элементом списка является список элементов каталога, составляющих путь от корня каталога к элементу каталога,
     * последним элементом списка является элемент каталога с указанным <code>id</code>.
     * Список может быть пустым если список <code>itemIdSet</code> пуст или содержит неверные данные
     * @throws IllegalArgumentException если <code>itemIdSet</code> равен <code>null</code>
     */
    // TODO обдумать, оптимизировать.
    @Transactional(readOnly = true)
    public List<List<CatalogItem>> getMergedPathList(Set<Integer> itemIdSet) {
        Assert.notNull(itemIdSet);

        List<List<CatalogItem>> list = new ArrayList<List<CatalogItem>>(itemIdSet.size());
        for (Integer id : itemIdSet) {
            list.add(catalogItemDAO.getPath(id));
        }

        for (int i = 0; i < list.size(); i++) {
            List<CatalogItem> targetPath = list.get(i);

            for (int j = 0; j < list.size(); j++) {
                if (i != j) {
                    List<CatalogItem> sourcePath = list.get(j);

                    List<Integer> targetPathIds = new ArrayList<Integer>(targetPath.size());
                    for (CatalogItem item : targetPath) {
                        targetPathIds.add(item.getId());
                    }

                    List<Integer> sourcePathIds = new ArrayList<Integer>(sourcePath.size());
                    for (CatalogItem item : sourcePath) {
                        sourcePathIds.add(item.getId());
                    }

                    if (Collections.indexOfSubList(sourcePathIds, targetPathIds) == 0) {
                        list.remove(i);
                        i--;
                        break;
                    }
                }
            }
        }
        
        return list;
    }

    /**
     * Возвращает количество активных разделов и категорий каталога
     */
    @Transactional(readOnly = true)
    public int getActiveCatalogItemCount() {
        return catalogItemDAO.getActiveCatalogItemCount();
    }

    /**
     * Возвращает категорию каталога по идентификационному номеру
     * @param categoryId
     * @return Категорию или <code>null</code> элемента каталога с таким номером не существует
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code> 
     */
    @Transactional(readOnly = true)
    public Category getCategoryById(Integer categoryId) {
        return categoryDAO.getCategoryById(categoryId);
    }

    /**
     * Возвращает отсортированный список категорий раздела всех уровней вложенности.
     * @param sectionId идентификатор раздела или <code>null</code> для получения списка всех категорий каталога
     * @return Список разделов и категорий или пустой список если раздел с <code>sectionId</code> не существует или не содержит категорий
     */
    public List<CatalogItem> getSubCategories(Integer sectionId) {
        List<CatalogItem> result = new ArrayList<CatalogItem>();
        for (CatalogItem item : getSubTree(sectionId)) {
            if (isCategoryItem(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Возвращает карту <b>категорий</b> раздела всех уровней вложенности.
     * @param sectionId идентификатор раздела или <code>null</code> для получения карты всех категорий каталога
     * @return Карту категорий или пустую карту если раздел с <code>sectionId</code> не существует или не содержит категорий
     */
    @Transactional(readOnly = true)
    public Map<Integer, Category> getSubCategoriesMap(Integer sectionId) {
        Set<Integer> categoryIdSet = new HashSet<Integer>();
        List<CatalogItem> subTreeList = catalogItemDAO.getSubTree(sectionId);
        for (CatalogItem item : subTreeList) {
            if (isCategoryItem(item)) {
                categoryIdSet.add(item.getId());
            }
        }
        if (categoryIdSet.isEmpty()) {
            return Collections.emptyMap();
        }
        return categoryDAO.getCategoryMap(categoryIdSet);
    }

    /**
     * Возвращает карту категорий.
     * @param categoryIdSet набор <code>id</code> категорий
     * @return Карту категорий. Ключем является <code>id</code> категории.
     * Карта может быть пустой если список <code>categoryIdSet</code> пуст или содержит неверные данные
     * @throws IllegalArgumentException если <code>categoryIdSet</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Map<Integer, Category> getCategoryMap(Set<Integer> categoryIdSet) {
        return categoryDAO.getCategoryMap(categoryIdSet);
    }

    /**
     * Проверяет является ли элемент каталога дочерним на любом уровне относительно другого элемента каталога.
     * @param item проверяемый элемент каталога
     * @param parentItem предполагаемый родительский элемент каталога
     * @throws IllegalArgumentException если любой из аргументов равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public boolean isChildItem(CatalogItem item, CatalogItem parentItem) {
        return catalogItemDAO.isChildItem(item, parentItem);
    }

    @Transactional(readOnly = true)
    public boolean isEquals(CatalogItem item, CatalogItem otherItem) {
        return catalogItemDAO.isEquals(item, otherItem);
    }

    public boolean isSectionItem(CatalogItem item) {
        Assert.notNull(item);
        return (CatalogItem.Type.SECTION.equals(item.getType()));
    }

    public boolean isCategoryItem(CatalogItem item) {
        Assert.notNull(item);
        return (CatalogItem.Type.CATEGORY.equals(item.getType()));
    }

    /**
     * Возвращает список категорий, отсортированный по <code>id</code>.
     * @return Список категорий или пустой список если категории отсутствуют
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }
    
    /**
     * Возвращает список активных категорий, 
     * в которых имеются доступные предложения связанные с брендом.
     * Сортировка по порядку и вложенности.
     * @param brandId идентификатор бренда
     * @return Список категорий или пустой список если категории отсутствуют
     */
    @Transactional(readOnly = true)
    public List<CatalogItem> getBrandActiveCategoryList(Integer brandId) {
        return catalogItemDAO.getBrandActiveCategoryList(brandId);
    }

    /**
     * Возвращает список активных категорий,
     * в которых имеются доступные предложения Поставщика.
     * Сортировка по порядку и вложенности.
     * @param companyId идентификатор поставщика
     * @return Список категорий или пустой список если категории отсутствуют
     */
    @Transactional(readOnly = true)
    public List<CatalogItem> getCompanyActiveCategoryList(Integer companyId) {
        return catalogItemDAO.getCompanyActiveCategoryList(companyId);
    }

    private void moveItem(CatalogItem item, Integer afterId, Integer sectionId, boolean forceActive) throws CatalogItemException {
        catalogItemDAO.moveItem(item.getId(), afterId, sectionId);
        if (forceActive) {
            CatalogItem parentItem = catalogItemDAO.getParent(item.getId());
            if (parentItem != null && !parentItem.getActive()) {
                item.setActive(false);
                if (isSectionItem(item)) {
                    catalogItemDAO.updateSectionTreeActive(item);
                } else if (isCategoryItem(item)) {
                    catalogItemDAO.updateItemActive(item);
                }
            }
        }
    }
}
// TODO Оптимизировать получение свойств canDelete, canEdit элементов каталога на основе ранее полученного дерева.
// TODO Получение количества товарных предложений в категории также можно (возможно) осуществить одним запросом.

