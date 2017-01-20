package ru.nullpointer.storefront.dao;

import java.util.List;
import ru.nullpointer.storefront.domain.param.Param;

/**
 *
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
public interface ParamDAO {

    /**
     * Возвращает параметр по идентификатору.
     * @return Параметр или <code>null</code> если параметра с таким идентификатором не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    Param getParamById(Integer id);

    /**
     * Возвращает список всех параметров, отсортированный в алфавитном порядке наименований.
     * @return Список параметров или пустой список если параметры отсутствуют
     */
    List<Param> getAllParams();

    /**
     * Возвращает список всех параметров группы, отсортированный по <code>ordinal</code>.
     * @param paramGroupId идентификатор группы параметров
     * @return Список параметров или пустой список если параметры в группе отсутствуют
     * @throws IllegalArgumentException если <code>paramGroupId</code> равен <code>null</code>
     */
    List<Param> getParamsByParamGroupId(Integer paramGroupId);

    /**
     * Возвращает список параметров, отсортированный по ИД параметра
     * @param psdId
     * @return
     */
    List<Param> getParamListByDescriptorId(Integer psdId);

    /**
     * Добавляет параметр. 
     * Устанавливает параметру полученный <code>id</code>.
     * Устанавливает параметру максимальный порядковый номер.
     * @param param добавляемый параметр
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     * @throws TODO Exception если нарушена связь со связанными данными
     */
    void insert(Param param);

    /**
     * Обновляет информацию о параметре.
     * Не обновляет идентификатор, порядок, связь с дескриптором набора параметров, связь с группой параметров, наименование столбца, тип.
     * @param param обновляемый параметр
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void updateInfo(Param param);

    /**
     * Удаляет параметр.
     * @param id идентификатор параметра
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     * @throws TODO Exception если нарушена связь со связанными данными
     */
    void delete(Integer id);

    /**
     * Устанавливает порядок параметра в группе.
     * Переупорядочивает остальные параметры в группе.
     * @param param параметр
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code> или обязательные атрибуты не "валидны"
     */
    void updateOrder(Param param);

    /**
     * Перемещает параметр в новую группу последним по порядку.
     * Переупорядочивает параметры в старой группе
     * Устанавливает параметру максимальный порядковый номер в новой группе.
     * @param param обновляемый параметр
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void updateParamGroup(Param param);

    /**
     * Обновляет атрибут параметра <code>base</code>.
     * @param param обновляемый параметр
     * @throws IllegalArgumentException если <code>param</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void updateBase(Param param);
}
