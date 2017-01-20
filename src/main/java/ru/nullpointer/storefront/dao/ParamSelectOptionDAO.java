package ru.nullpointer.storefront.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nullpointer.storefront.domain.param.ParamSelectOption;

/**
 *
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
/*
 * Описывает свойства варианта выбора выборочного параметра (<code>Param.Type.SELECT</code>)
 * Связь с сущностью "param" M:1
 */
public interface ParamSelectOptionDAO {

    /**
     * Возвращает вариант выбора выборочного параметра по идентификатору.
     * @return Вариант выбора выборочного параметра или <code>null</code> если варианта выбора выборочного параметра с таким идентификатором не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    ParamSelectOption getParamSelectOptionById(Integer id);

    /**
     * Возвращает список всех вариантов выбора выборочного параметра, связанных с конкретным параметром, отсортированный по <code>ordinal</code>.
     * @param paramId идентификатор параметра
     * @return Список вариантов выбора выборочного параметра или пустой список если варианты выбора выборочного параметра, связанные с конкретным параметром, отсутствуют
     * @throws IllegalArgumentException если <code>paramId</code> равен <code>null</code>
     */
    List<ParamSelectOption> getParamSelectOptionsByParamId(Integer paramId);

    /**
     * Возвращает карту вариантов выбора выборочного параметра
     * Ключем карты является ИД параметра
     * Значением - список вариантов выбора, отсортированный по <code>ordinal</code>.
     * @param paramIdSet список идентификационных номеров параметров
     * @return
     */
    Map<Integer, List<ParamSelectOption>> getParamSelectOptionMap(Set<Integer> paramIdSet);

    /**
     * Добавляет вариант выбора выборочного параметра. 
     * Устанавливает варианту выбора выборочного параметра полученный <code>id</code>.
     * Устанавливает максимальный порядковый номер.
     * @param paramSelectOption добавляемый вариант выбора выборочного параметра
     * @throws IllegalArgumentException если <code>paramSelectOption</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void insert(ParamSelectOption paramSelectOption);

    /**
     * Обновляет информацию о варианте выбора выборочного параметра.
     * Не обновляет идентификатор, порядок, связь с параметром.
     * @param paramSelectOption обновляемый вариант выбора выборочного параметра
     * @throws IllegalArgumentException если <code>paramSelectOption</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void updateInfo(ParamSelectOption paramSelectOption);

    /**
     * Удаляет вариант выбора выборочного параметра.
     * Ссылки на удаляемый вариант выбора из наборов параметров обнуляются автоматически.
     * @param id идентификатор варианта выбора выборочного параметра
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    void delete(Integer id);

    /**
     * Обновляет порядок варианта выбора выборочного параметра.
     * Переупорядочивает остальные варианты выбора.
     * @param paramSelectOptions обновляемый вариант выбора выборочного параметра
     * @throws IllegalArgumentException если <code>paramSelectOption</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void updateOrder(ParamSelectOption paramSelectOption);
}
