package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import com.ibatis.sqlmap.client.SqlMapClient;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import ru.nullpointer.storefront.dao.CatalogItemDAO;
import ru.nullpointer.storefront.dao.CatalogItemException;
import ru.nullpointer.storefront.domain.CatalogItem;

/**
 *
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class IBatisCatalogItemDAO extends SqlMapClientDaoSupport implements CatalogItemDAO {

    private Logger logger = LoggerFactory.getLogger(IBatisCatalogItemDAO.class);
    //
    private static final int PATH_ELEMENT_SIZE = 2;
    private static final int PATH_ELEMENT_VALUE_RADIX = 10;
    private static final int PATH_ELEMENT_VALUE_MIN = 0;
    private static final int PATH_ELEMENT_VALUE_MAX = (int) (Math.pow(PATH_ELEMENT_VALUE_RADIX, PATH_ELEMENT_SIZE)) - 1;
    private static final String PATH_ELEMENT_VALUE_FORMAT = "%02d";
    //

    private CatalogItem queryForObject(String select, Object param) {
        return (CatalogItem) getSqlMapClientTemplate().queryForObject(select, param);
    }

    private List<CatalogItem> queryForList(String select, Object param) {
        return (List<CatalogItem>) getSqlMapClientTemplate().queryForList(select, param);
    }

    private Integer queryForSize(String select, Object param) {
        return (Integer) getSqlMapClientTemplate().queryForObject(select, param);
    }

    @Override
    public int getItemLevel(CatalogItem item) {
        Assert.notNull(item);
        String path = item.getPath();
        validateItemPath(path);
        int level = getItemPathLevel(path) / PATH_ELEMENT_SIZE;
        return level;
    }

    @Override
    public List<CatalogItem> getChildrenList(Integer sectionId, boolean onlyActive) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sectionId", sectionId);
        if (onlyActive) {
            paramMap.put("onlyActive", true);
        }
        return queryForList("CatalogItem.selectChildrenBySectionId", paramMap);
    }

    @Override
    public List<CatalogItem> getSubTree(Integer sectionId) {
        return queryForList("CatalogItem.selectSubTreeBySectionId", sectionId);
    }

    @Override
    public CatalogItem getParent(Integer itemId) {
        Assert.notNull(itemId);

        CatalogItem item = queryForObject("CatalogItem.selectById", itemId);
        Assert.notNull(item);

        String path = item.getPath();
        String parentPath = path.substring(0, path.length() - PATH_ELEMENT_SIZE);

        return queryForObject("CatalogItem.selectByPath", parentPath);
    }

    @Override
    public void addItem(CatalogItem item, Integer sectionId) throws CatalogItemException {
        Assert.notNull(item);

        // TODO проверку в сервис?

        CatalogItem parentItem = null;

        if (sectionId != null) {
            parentItem = queryForObject("CatalogItem.selectById", sectionId);
            Assert.notNull(parentItem);
        }

        if (parentItem != null) {
            Assert.isTrue(parentItem.getType() == CatalogItem.Type.SECTION);
        } else {
            Assert.isTrue(item.getType() != CatalogItem.Type.CATEGORY);
        }

        Assert.notNull(item.getType());
        Assert.hasText(item.getName());

        List<CatalogItem> parentChildren = getChildrenList(parentItem != null ? sectionId : null, false);
        String itemPath = getItemPathFromOrder(parentChildren.size(), (parentItem != null ? parentItem.getPath() : null));
        validateItemPath(itemPath);
        item.setPath(itemPath);

        getSqlMapClientTemplate().insert("CatalogItem.insert", item);
    }

    @Override
    public void deleteItem(Integer itemId) throws CatalogItemException {
        Assert.notNull(itemId);

        CatalogItem deleteItem = queryForObject("CatalogItem.selectById", itemId);
        Assert.notNull(deleteItem);

        // TODO Assert.isTrue(offers.size() == 0); проверка на наличие товарных предложений (TO SERVICE)

        CatalogItem deletedItemParent = getParent(deleteItem.getId());
        List<CatalogItem> neighborItems = getSubTree(deletedItemParent != null ? deletedItemParent.getId() : null);

        // список удаляемых разделов и категорий каталога
        List<CatalogItem> deleteItems = new ArrayList<CatalogItem>();
        deleteItems.add(deleteItem);

        // список измененных разделов и категорий каталога
        List<CatalogItem> changedItems = new ArrayList<CatalogItem>();

        // Определить необходимые индексы в списке всех соседей
        int deleteItemIndex = -1;
        for (int i = 0; i < neighborItems.size(); i++) {
            if (itemId.equals(neighborItems.get(i).getId())) {
                deleteItemIndex = i;
                break;
            }
        }
        Assert.isTrue(deleteItemIndex >= 0);
        int neighborDownItemIndex = getItemNeighborIndex(neighborItems, deleteItemIndex);
        int neighborDownItemsIndexEnd = getItemEndIndex(neighborItems, neighborDownItemIndex, true);

        // Получить все удаляемые подразделы и категории
        int deleteItemIndexEnd = getItemEndIndex(neighborItems, deleteItemIndex, false);
        for (int i = deleteItemIndex + 1; i <= deleteItemIndexEnd; i++) {
            deleteItems.add(neighborItems.get(i));
        }

        // сдвинуть нижних бывших соседей вверх на 1 порядок
        if (neighborDownItemIndex >= 0) {
            shiftItemsPath(-1, neighborItems, neighborDownItemIndex, neighborDownItemsIndexEnd, changedItems);
        }

        // зафиксировать в БД
        multipleDeleteItemsAndUpdateItemsPath(deleteItems, changedItems);
    }

    @Override
    public void updateItemInfo(CatalogItem item) {
        Assert.notNull(item);
        Assert.notNull(item.getId());
        getSqlMapClientTemplate().update("CatalogItem.updateInfo", item);
    }

    @Override
    public void updateItemActive(CatalogItem item) {
        Assert.notNull(item);
        Assert.notNull(item.getId());
        getSqlMapClientTemplate().update("CatalogItem.updateItemActive", item);
    }

    @Override
    public void updateSectionTreeActive(CatalogItem sectionItem) {
        Assert.notNull(sectionItem);
        Assert.notNull(sectionItem.getId());
        getSqlMapClientTemplate().update("CatalogItem.updateSectionTreeActive", sectionItem);
    }

    @Override
    public void moveItem(Integer itemId, Integer afterId, Integer sectionId) throws CatalogItemException {
        Assert.notNull(itemId);
        Assert.isTrue(!(itemId.equals(afterId)));
        Assert.isTrue(!(itemId.equals(sectionId)));
        if (afterId != null) {
            Assert.isNull(sectionId);
        }

        // список всех разделов и категорий каталога, по которому будут производится изменения в иерархии каталога
        List<CatalogItem> catalogTree = getSubTree(null);

        // Определить необходимые индексы в списке, выполнить проверку субординации
        int lastIndex = catalogTree.size() - 1;
        int moveItemIndex = -1;
        int moveItemIndexEnd = -1;
        int neighborDownItemIndex = -1;
        int neighborDownItemsIndexEnd = -1;
        int afterItemIndex = -1;
        int afterItemsIndexEnd = -1;
        int sectionItemIndex = -1;
        int sectionItemsIndexEnd = lastIndex;

        for (int i = 0; i <= lastIndex; i++) {
            Integer id = catalogTree.get(i).getId();
            if (id.equals(itemId)) {
                moveItemIndex = i;
            } else if (id.equals(afterId)) {
                afterItemIndex = i;
            } else if (id.equals(sectionId)) {
                sectionItemIndex = i;
            }
        }

        Assert.isTrue(moveItemIndex >= 0);

        if (afterId != null) {
            Assert.isTrue(afterItemIndex >= 0);
        }
        if (sectionId != null) {
            Assert.isTrue(sectionItemIndex >= 0);
        }

        if (sectionItemIndex >= 0) {
            Assert.isTrue(catalogTree.get(sectionItemIndex).getType() == CatalogItem.Type.SECTION);
        }
        if (catalogTree.get(moveItemIndex).getType() == CatalogItem.Type.CATEGORY) {
            Assert.isTrue(!(afterId == null && sectionId == null));
            if (afterItemIndex >= 0) {
                Assert.isTrue(getItemPathLevel(catalogTree.get(afterItemIndex).getPath()) > getTopLevel());
            }
        }

        moveItemIndexEnd = getItemEndIndex(catalogTree, moveItemIndex, false);
        neighborDownItemIndex = getItemNeighborIndex(catalogTree, moveItemIndex);
        neighborDownItemsIndexEnd = getItemEndIndex(catalogTree, neighborDownItemIndex, true);
        afterItemsIndexEnd = getItemEndIndex(catalogTree, afterItemIndex, true);
        sectionItemsIndexEnd = sectionItemIndex >= 0 ? getItemEndIndex(catalogTree, sectionItemIndex, false) : lastIndex;

        Assert.isTrue(!(afterItemIndex >= moveItemIndex && afterItemIndex <= moveItemIndexEnd));
        Assert.isTrue(!(sectionItemIndex >= moveItemIndex && sectionItemIndex <= moveItemIndexEnd));

        // список измененных разделов и категорий каталога
        List<CatalogItem> changedItems = new ArrayList<CatalogItem>();
        String moveItemPathOld = catalogTree.get(moveItemIndex).getPath();
        String moveItemPathNew = null;

        // сдвинуть нижних бывших соседей вверх на 1 порядок
        if (neighborDownItemIndex >= 0) {
            shiftItemsPath(-1, catalogTree, neighborDownItemIndex, neighborDownItemsIndexEnd, changedItems);
        }

        // сдвинуть нижних новых соседей вниз на 1 порядок
        if (afterItemIndex >= 0) {
            shiftItemsPath(1, catalogTree, afterItemIndex, afterItemsIndexEnd, changedItems);
        }

        // Определить новый путь
        if (afterItemIndex >= 0) {
            String afterItemPath = catalogTree.get(afterItemIndex).getPath();
            String sectionPath = getItemParentPath(afterItemPath);
            int moveItemOrderNew = getItemOrderFromPath(afterItemPath, null) - 1;
            moveItemPathNew = getItemPathFromOrder(moveItemOrderNew, sectionPath);
        } else {
            String sectionPath = (sectionItemIndex >= 0 ? catalogTree.get(sectionItemIndex).getPath() : null);
            int level = getNextItemPathLevel(sectionPath);
            int moveItemOrderNew = 0;
            for (int i = sectionItemsIndexEnd; i > sectionItemIndex; i--) {
                if (!(i >= moveItemIndex && i <= moveItemIndexEnd)) {
                    String path = catalogTree.get(i).getPath();
                    if (getItemPathLevel(path) == level) {
                        moveItemOrderNew = getItemOrderFromPath(path, level) + 1;
                        break;
                    }
                }
            }
            moveItemPathNew = getItemPathFromOrder(moveItemOrderNew, sectionPath);
        }
        validateItemPath(moveItemPathNew);
        if (moveItemPathOld.equals(moveItemPathNew)) {
            logger.debug("###: moveItem - nothing move");
            return;
        }

        // Применить новый путь
        setItemsPath(moveItemPathNew, catalogTree, moveItemIndex, moveItemIndexEnd, changedItems);

        if (changedItems.isEmpty()) {
            logger.debug("###: moveItem - nothing move");
            return;
        }

        // зафиксировать в БД
        multipleUpdateItemsPath(changedItems, true);
    }

    @Override
    public List<CatalogItem> getPopularCategories(Integer sectionId) {
        Assert.notNull(sectionId);

        // TODO: реализовать индекс популярности
        List<CatalogItem> list = queryForList("CatalogItem.selectPopularCategoriesBySectionId", sectionId);

        // Пока возвращаем столько подкатегорий, чтобы суммарная длина их названий не превышала 100 символов
        // 100 символов ~= 3 строки по 30 символов + разделители
        final int MAX_TOTAL_NAME_LENGTH = 100;
        int totalLen = 0;
        List<CatalogItem> result = new ArrayList<CatalogItem>();
        for (CatalogItem item : list) {
            totalLen += item.getName().length();
            if (totalLen > MAX_TOTAL_NAME_LENGTH) {
                break;
            }
            result.add(item);
        }
        return result;
    }

    @Override
    public CatalogItem getItemById(Integer id) {
        Assert.notNull(id);
        CatalogItem item = queryForObject("CatalogItem.selectById", id);
        return item;
    }

    @Override
    public CatalogItem getSectionItemById(Integer id) {
        Assert.notNull(id);
        CatalogItem item = queryForObject("CatalogItem.selectById", id);
        if (item != null && item.getType() != CatalogItem.Type.SECTION) {
            return null;
        }
        return item;
    }

    @Override
    public CatalogItem getCategoryItemById(Integer id) {
        Assert.notNull(id);
        CatalogItem item = queryForObject("CatalogItem.selectById", id);
        if (item != null && item.getType() != CatalogItem.Type.CATEGORY) {
            return null;
        }
        return item;
    }

    @Override
    public List<CatalogItem> getPath(Integer id) {
        CatalogItem item = queryForObject("CatalogItem.selectById", id);
        if (item == null) {
            return Collections.emptyList();
        }

        String path = item.getPath();

        List<String> pathElementList = new ArrayList<String>();

        for (int i = 0; i < path.length(); i += PATH_ELEMENT_SIZE) {
            pathElementList.add(path.substring(0, i + PATH_ELEMENT_SIZE));
        }

        return queryForList("CatalogItem.selectPath", pathElementList);
    }

    @Override
    public int getActiveCatalogItemCount() {
        return queryForSize("CatalogItem.countActive", null);
    }

    @Override
    public List<CatalogItem> getBrandActiveCategoryList(Integer brandId) {
        Assert.notNull(brandId);
        return queryForList("CatalogItem.selectActiveByBrandId", brandId);
    }

    @Override
    public List<CatalogItem> getCompanyActiveCategoryList(Integer companyId) {
        Assert.notNull(companyId);
        return queryForList("CatalogItem.selectActiveByCompanyId", companyId);
    }

    @Override
    public boolean isChildItem(CatalogItem item, CatalogItem parentItem) {
        Assert.notNull(item);
        Assert.notNull(item.getId());
        Assert.hasText(item.getPath());
        Assert.notNull(parentItem);
        Assert.notNull(parentItem.getId());
        Assert.hasText(parentItem.getPath());
        if (item.getPath().equals(parentItem.getPath())) {
            return false;
        }
        return item.getPath().startsWith(parentItem.getPath());
    }

    @Override
    public boolean isEquals(CatalogItem item, CatalogItem otherItem) {
        Assert.notNull(item);
        Assert.notNull(item.getId());
        Assert.hasText(item.getPath());
        if (item.getId().equals(otherItem.getId())
                && item.getPath().equals(otherItem.getPath())) {
            return true;
        }
        return false;
    }

    /**
     * Проверяет значение порядка в списке дочерних родительского раздела.
     * @param order проверяемое значение порядка
     * @throws CatalogItemException если <code>order</code> меньше <code>PATH_ELEMENT_VALUE_MIN</code> или больше <code>PATH_ELEMENT_VALUE_MAX</code>
     */
    public void validateItemOrder(int order) throws CatalogItemException {
        if (order < 0 || order > PATH_ELEMENT_VALUE_MAX) {
            throw new CatalogItemException("Порядок раздела или категории не может быть меньше " + Integer.toString(PATH_ELEMENT_VALUE_MIN) + " или больше " + Integer.toString(PATH_ELEMENT_VALUE_MAX));
        }
    }

    private void validateItemPath(String path) {
        Assert.hasText(path);
        validateItemLevel(getItemPathLevel(path));
    }

    private void validateItemLevel(int level) {
        Assert.isTrue(level % PATH_ELEMENT_SIZE == 0);
    }

    private String getItemPathFromOrder(int order, String parentPath) throws CatalogItemException {
        validateItemOrder(order);
        return (parentPath != null ? parentPath : "").concat(String.format(PATH_ELEMENT_VALUE_FORMAT, order));
    }

    private int getItemOrderFromPath(String path, Integer orderLevel) {
        int level = (orderLevel == null ? path.length() : orderLevel.intValue());
        validateItemLevel(level);
        return Integer.parseInt(path.substring(level - PATH_ELEMENT_SIZE, level), PATH_ELEMENT_VALUE_RADIX);
    }

    private String getItemParentPath(String path) {
        validateItemPath(path);
        return path.substring(0, path.length() - PATH_ELEMENT_SIZE);
    }

    private int getItemPathLevel(String path) {
        int level = path.length();
        validateItemLevel(level);
        return level;
    }

    private int getTopLevel() {
        return PATH_ELEMENT_SIZE;
    }

    private int getNextItemPathLevel(String path) {
        int level = (path != null ? path.length() + PATH_ELEMENT_SIZE : PATH_ELEMENT_SIZE);
        validateItemLevel(level);
        return level;
    }

    private String changeLeadingItemPath(String path, String leadingOld, String leadingNew) {
        String newPath = path.replaceFirst(leadingOld, leadingNew);
        validateItemPath(newPath);
        return newPath;
    }

    private int getItemEndIndex(List<CatalogItem> subTree, int startIndex, boolean strict) {
        int endIndex = -1;
        if (startIndex >= 0) {
            int lastIndex = subTree.size() - 1;
            endIndex = lastIndex;
            int level = getItemPathLevel(subTree.get(startIndex).getPath());
            for (int e = startIndex + 1; e <= lastIndex; e++) {
                if (strict) {
                    if (getItemPathLevel(subTree.get(e).getPath()) < level) {
                        endIndex = e - 1;
                        break;
                    }
                } else {
                    if (getItemPathLevel(subTree.get(e).getPath()) <= level) {
                        endIndex = e - 1;
                        break;
                    }
                }
            }
        }
        return endIndex;
    }

    private int getItemNeighborIndex(List<CatalogItem> subTree, int itemIndex) {
        int neighborIndex = -1;
        if (itemIndex >= 0) {
            int level = subTree.get(itemIndex).getPath().length();
            for (int e = itemIndex + 1; e < subTree.size(); e++) {
                int l = getItemPathLevel(subTree.get(e).getPath());
                if (l <= level) {
                    if (l == level) {
                        neighborIndex = e;
                    }
                    break;
                }
            }
        }
        return neighborIndex;
    }

    private void shiftItemsPath(int delta, List<CatalogItem> subTree, int beginIndex, int endIndex, List<CatalogItem> changedList) throws CatalogItemException {
        String path = subTree.get(beginIndex).getPath();
        String parentPath = getItemParentPath(path);
        int level = getItemPathLevel(path);
        for (int i = beginIndex; i <= endIndex; i++) {
            CatalogItem item = subTree.get(i);
            String itemPath = item.getPath();
            int order = getItemOrderFromPath(itemPath, level);
            String leadingOld = getItemPathFromOrder(order, parentPath);
            String leadingNew = getItemPathFromOrder(order + delta, parentPath);
            item.setPath(changeLeadingItemPath(itemPath, leadingOld, leadingNew));
            if (changedList != null) {
                changedList.add(item);
            }
        }
    }

    private void setItemsPath(String path, List<CatalogItem> subTree, int beginIndex, int endIndex, List<CatalogItem> changedList) throws CatalogItemException {
        if (beginIndex >= 0) { // separate method?
            String itemPath = subTree.get(beginIndex).getPath();
            for (int i = beginIndex; i <= endIndex; i++) {
                CatalogItem item = subTree.get(i);
                item.setPath(changeLeadingItemPath(item.getPath(), itemPath, path));
                changedList.add(item);
            }
        }
    }

    private void multipleUpdateItemsPath(List<CatalogItem> updateItems, boolean ensureIntegrity) {
        if (updateItems.isEmpty()) {
            logger.debug("###: multipleUpdateItemsPath - nothing update");
            return;
        }

        SqlMapClientTemplate sqlMapClientTemplate = getSqlMapClientTemplate();
        SqlMapClient sqlMapClient = sqlMapClientTemplate.getSqlMapClient();

        try {
            try {
                sqlMapClient.startTransaction();
                sqlMapClient.startBatch();

                if (ensureIntegrity) {
                    // TODO обдумать (способы множественного обновления записей в БД, ...)
                    // использование префикса при первом множественном обновлении для исключения нарушения уникальности поля ci_path
                    for (CatalogItem item : updateItems) {
                        item.setPath("#" + item.getPath());
                        sqlMapClientTemplate.update("CatalogItem.updateItemPath", item, 1);
                    }
                }
                // повторное обновление без префикса 
                for (CatalogItem item : updateItems) {
                    item.setPath(item.getPath().substring(1));
                    sqlMapClientTemplate.update("CatalogItem.updateItemPath", item, 1);
                }

                sqlMapClient.executeBatch();
                sqlMapClient.commitTransaction();
            } finally {
                sqlMapClient.endTransaction();
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void multipleDeleteItemsAndUpdateItemsPath(List<CatalogItem> deleteItems, List<CatalogItem> updateItems) {
        if (deleteItems.isEmpty()) {
            logger.debug("###: multipleDeleteItemsAndUpdateItemsPath - nothing delete");
            return;
        }

        SqlMapClientTemplate sqlMapClientTemplate = getSqlMapClientTemplate();
        SqlMapClient sqlMapClient = sqlMapClientTemplate.getSqlMapClient();

        try {
            try {
                sqlMapClient.startTransaction();
                sqlMapClient.startBatch();

                for (CatalogItem item : deleteItems) {
                    // TODO cascade delete (если надо)
                    sqlMapClientTemplate.delete("CatalogItem.delete", item.getId(), 1);
                }
                for (CatalogItem item : updateItems) {
                    sqlMapClientTemplate.update("CatalogItem.updateItemPath", item, 1);
                }

                sqlMapClient.executeBatch();
                sqlMapClient.commitTransaction();
            } finally {
                sqlMapClient.endTransaction();
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
