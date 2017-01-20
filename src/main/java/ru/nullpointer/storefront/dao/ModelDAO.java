package ru.nullpointer.storefront.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 *
 * @author Alexander Yastrebov
 */
public interface ModelDAO {

    /**
     * Возвращает список моделей в категории
     * @param categoryId
     * @param pageConfig
     * @return
     */
    List<Model> getCategoryModelList(Integer categoryId, PageConfig pageConfig);

    /**
     * Возвращает количество моделей в категории
     * @param categoryId
     * @return
     */
    int getCategoryModelCount(Integer categoryId);

    /**
     * Возвращает карту количества моделей по категориям
     * Ключем карты является идентификационный номер категории,
     * значением - количество моделей в этой категории
     * @param categoryIdSet набор ИД категорий
     * @return
     * @throws IllegalArgumentException если <code>categoryIdSet</code> равен <code>null</code>
     */
    Map<Integer, Integer> getCategoryModelCountMap(Set<Integer> categoryIdSet);

    /**
     * Возвращает модель по идентификатору
     * @param id идентификатор модели
     * @return модель или <code>null</code> если модели с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    Model getModelById(Integer id);

    /**
     * Возвращает карту моделей.
     * @param modelIdSet набор <code>id</code> моделей
     * @return Карту моделей.
     * Ключем является <code>id</code> модели.
     * Карта может быть пустой если список <code>modelIdSet</code> пуст или содержит неверные данные
     * @throws IllegalArgumentException если <code>modelIdSet</code> равен <code>null</code>
     */
    Map<Integer, Model> getModelMap(Set<Integer> modelIdSet);

    /**
     * Возвращает список моделей по вхождению текста в наименование
     * и ключевые слова
     * @param text
     * @param categoryId идентификатор категории, может быть <code>null</code>
     * @param brandId идентификатор бренда, может быть <code>null</code>
     * @param limit максимальное количество возвращаемых моделей
     * @return
     */
    List<Model> findModelListByText(String text, Integer categoryId, Integer brandId, int limit);

    /**
     * Добавляет модель.
     * Устанавливает модели полученный <code>id</code>.
     * @param model добавляемая модель
     * @throws IllegalArgumentException если <code>model</code> равен <code>null</code>
     */
    void insert(Model model);

    /**
     * Обновляет информацию о модели.
     * Не обновляет идентификатор, связь с категорией, связь с набором параметров.
     * @param model обновляемая модель
     * @throws IllegalArgumentException если <code>model</code> равен <code>null</code>
     */
    void updateInfo(Model model);

    /**
     * Удаляет модель.
     * @param id идентификатор модели
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    void delete(Integer id);
}
