package ru.nullpointer.storefront.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nullpointer.storefront.domain.Unit;

/**
 *
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public interface UnitDAO {

    /**
     * Возвращает карту единиц измерения категорий. 
     * @param unitIdSet набор <code>id</code> единиц измерения
     * @return Карту единиц измерения категорий. Ключем является <code>id</code> единицы измерения. Карта может быть пустой если список <code>unitIdSet</code> пуст или содержит неверные данные
     * @throws IllegalArgumentException если <code>unitIdSet</code> равен <code>null</code>
     */
    Map<Integer, Unit> getUnitMap(Set<Integer> unitIdSet);

    /**
     * Возвращает список всех единиц измерения категорий, отсортированный в алфавитном порядке наименований.
     * @return Список единиц измерения или пустой список если единицы измерения отсутствуют
     */
    List<Unit> getAllUnits();

    /**
     * Возвращает единицу измерения категорий по идентификатору
     * @param id идентификатор единицы измерения
     * @return единицу измерения или <code>null</code> если единицы измерения с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    Unit getUnitById(Integer id);

    /**
     * Возвращает список единиц измерения по вхождению текста в наименование
     * @param text
     * @return
     */
    List<Unit> findUnitListByName(String text);

    /**
     * Добавляет единицу измерения категорий. Устанавливает единице измерения полученный <code>id</code>.
     * @param unit добавляемая единица измерения
     * @throws IllegalArgumentException если <code>unit</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void addUnit(Unit unit);

    /**
     * Обновляет информацию о единице измерения категорий. Не обновляет идентификатор.
     * @param unit обновляемая единица измерения
     * @throws IllegalArgumentException если <code>unit</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void updateUnitInfo(Unit unit);

    /**
     * Удаляет единицу измерения категорий.
     * @param id идентификатор единицы измерения
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code> или единица измерения не может быть удалена
     * @throws TODO Exception если нарушена связь со связанными данными
     */
    void deleteUnit(Integer id);
}
