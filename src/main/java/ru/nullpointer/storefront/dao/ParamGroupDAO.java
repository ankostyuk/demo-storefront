package ru.nullpointer.storefront.dao;

import java.util.List;
import ru.nullpointer.storefront.domain.param.ParamGroup;

/**
 *
 * @author ankostyuk
 * @author Alexander Yastrebov
 */
public interface ParamGroupDAO {

    /**
     * Возвращает группу параметров по идентификатору.
     * @return Группу параметров или <code>null</code> если группы параметров с таким идентификатором не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    ParamGroup getParamGroupById(Integer id);

    /**
     * Возвращает список всех групп параметров, отсортированный в алфавитном порядке наименований.
     * @return Список групп параметров или пустой список если группы параметров отсутствуют
     */
    List<ParamGroup> getAllParamGroups();

    /**
     * Возвращает список всех групп параметров, связанных с конкретным дескриптором набора параметров, отсортированный по <code>ordinal</code>.
     * @param psdId идентификатор дескриптора набора параметров
     * @return Список групп параметров или пустой список если группы параметров, связанные с конкретным дескриптором набора параметров, отсутствуют
     * @throws IllegalArgumentException если <code>psdId</code> равен <code>null</code>
     */
    List<ParamGroup> getParamGroupsByParamSetDescriptorId(Integer psdId);

    /**
     * Добавляет группу параметров. 
     * Устанавливает группе параметров полученный <code>id</code>.
     * Устанавливает максимальный порядковый номер.
     * @param paramGroup добавляемая группа параметров
     * @throws IllegalArgumentException если <code>paramGroup</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void insert(ParamGroup paramGroup);

    /**
     * Обновляет информацию о группе параметров.
     * Не обновляет идентификатор, порядок, связь с дескриптором набора параметров.
     * @param paramGroup обновляемая группа параметров
     * @throws IllegalArgumentException если <code>paramGroup</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void updateInfo(ParamGroup paramGroup);

    /**
     * Удаляет группу параметров.
     * @param id идентификатор группы параметров
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     * @throws TODO Exception если нарушена связь со связанными данными
     */
    void delete(Integer id);

    /**
     * Устанавливает порядок группы параметров.
     * Переупорядочивает остальные группы параметров в категории.
     * @param paramGroups обновляемая группа параметров
     * @throws IllegalArgumentException если <code>paramGroup</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void updateOrder(ParamGroup paramGroup);
}
