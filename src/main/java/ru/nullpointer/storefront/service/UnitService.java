package ru.nullpointer.storefront.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.dao.UnitDAO;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Unit;

/**
 * 
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Service
public class UnitService {

    @Resource
    private UnitDAO unitDAO;
    @Resource
    private CategoryDAO categoryDAO;

    /**
     * Возвращает карту единиц измерения.
     * 
     * @param unitIdSet
     *            набор <code>id</code> единиц измерения
     * @return Карту единиц измерения. Ключем является <code>id</code> единицы
     *         измерения. Карта может быть пустой если список
     *         <code>unitIdSet</code> пуст или содержит неверные данные
     * @throws IllegalArgumentException
     *             если <code>unitIdSet</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Map<Integer, Unit> getUnitMap(Set<Integer> unitIdSet) {
        return unitDAO.getUnitMap(unitIdSet);
    }

    /**
     * Возвращает карту единиц измерения категорий.
     * Ключем является <code>id</code> категории.
     * Карта может быть пустой если список <code>categoryIdSet</code> пуст или содержит неверные данные
     * @param categoryIdSet набор <code>id</code> категорий
     * @return Карту единиц измерения.
     * @throws IllegalArgumentException если <code>categoryIdSet</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Map<Integer, Unit> getCategoryUnitMap(Set<Integer> categoryIdSet) {
        Assert.notNull(categoryIdSet);
        if(categoryIdSet.isEmpty()) {
            return Collections.emptyMap();
        }
        
        Map<Integer, Category> categoryMap = categoryDAO.getCategoryMap(categoryIdSet);
        Set<Integer> unitIdSet = new HashSet<Integer>();
        for (Category category : categoryMap.values()) {
            unitIdSet.add(category.getUnitId());
        }

        Map<Integer, Unit> unitMap = unitDAO.getUnitMap(unitIdSet);
        Map<Integer, Unit> resultMap = new HashMap<Integer, Unit>(categoryMap.size());
        for (Category category : categoryMap.values()) {
            resultMap.put(category.getId(), unitMap.get(category.getUnitId()));
        }
        return resultMap;
    }

    /**
     * Возвращает единицу измерения категорий по идентификатору
     * 
     * @param id
     *            идентификатор единицы измерения
     * @return единицу измерения или <code>null</code> если единицы измерения с
     *         таким <code>id</code> не существует
     * @throws IllegalArgumentException
     *             если <code>id</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Unit getUnitById(Integer id) {
        return unitDAO.getUnitById(id);
    }

    /**
     * Возвращает список всех единиц измерения категорий, отсортированный в
     * алфавитном порядке наименований.
     * 
     * @return Список единиц измерения или пустой список если единицы измерения
     *         отсутствуют
     */
    @Transactional(readOnly = true)
    public List<Unit> getAllUnits() {
        return unitDAO.getAllUnits();
    }

    @Transactional(readOnly = true)
    public List<Unit> findUnitListByName(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }

        return unitDAO.findUnitListByName(text);
    }

    /**
     * Добавляет единицу измерения категорий. Устанавливает единице измерения
     * полученный <code>id</code>.
     * 
     * @param unit
     *            добавляемая единица измерения
     * @throws IllegalArgumentException
     *             если <code>unit</code> равен <code>null</code> или
     *             обязательные атрибуты не "валидны"
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void addUnit(Unit unit) {
        unitDAO.addUnit(unit);
    }

    /**
     * Обновляет информацию о единице измерения категорий. Не обновляет
     * идентификатор.
     * 
     * @param unit
     *            обновляемая единица измерения
     * @throws IllegalArgumentException
     *             если <code>unit</code> равен <code>null</code> или
     *             обязательные атрибуты не "валидны"
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void updateUnitInfo(Unit unit) {
        unitDAO.updateUnitInfo(unit);
    }

    /**
     * Удаляет единицу измерения категорий.
     * 
     * @param unit
     *            удаляемая единица измерения
     * @throws IllegalArgumentException
     *             если <code>unit</code> равен <code>null</code> или
     *             обязательные атрибуты не "валидны"
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = false)
    public void deleteUnit(Unit unit) {
        Assert.notNull(unit);
        Assert.isTrue(canDeleteUnit(unit));
        unitDAO.deleteUnit(unit.getId());
    }

    /**
     * Проверяет возможность удаления единицы измерения категорий.
     * 
     * @param unit
     *            единица измерения
     * @return Возможность удаления - <code>true</code>, иначе -
     *         <code>false</code>
     * @throws IllegalArgumentException
     *             если <code>unit</code> равен <code>null</code>
     */
    @Secured("ROLE_MANAGER_CATALOG")
    @Transactional(readOnly = true)
    public boolean canDeleteUnit(Unit unit) {
        Assert.notNull(unit);
        Assert.notNull(unit.getId());

        List<Category> categories = categoryDAO.getAllCategories();
        for (Category category : categories) {
            if (category.getUnitId().equals(unit.getId())) {
                return false;
            }
        }

        return true;
    }
}
// TODO лучше сделать unitService.сanDeleteUnit(Integer unitId), чтобы лишний
// раз не делать в контроллере выборку Unit по id

