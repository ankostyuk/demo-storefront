package ru.nullpointer.storefront.dao.impl;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.util.Assert;

/**
 * Вспомогательный класс для работы с таблицами, содержащими упорядоченные записи.
 * Каждая запись в такой таблице должна удовлетворять условиям:
 * - наличие первичного ключа типа Integer (idColumn)
 * - наличие колонки, по которой записи группируются (groupColumn)
 * - наличие колонки содержащий порядковый номер записи типа Integer в группе (ordinalColumn).
 *   Колонка содержащая порядковый номер должна поддерживать значение <code>null</code>
 * 
 * @author Alexander Yastrebov
 */
public class OrdinalHelper {

    private Logger logger = LoggerFactory.getLogger(OrdinalHelper.class);
    //
    private String tableName;
    private String idColumn;
    private String groupColumn;
    private String ordinalColumn;

    /**
     * @param tableName имя таблицы
     * @param idColumn имя первичного ключа
     * @param groupColumn имя групповой колонки
     * @param ordinalColumn имя порядковой колонки
     */
    public OrdinalHelper(String tableName, String idColumn, String groupColumn, String ordinalColumn) {
        this.tableName = tableName;
        this.idColumn = idColumn;
        this.groupColumn = groupColumn;
        this.ordinalColumn = ordinalColumn;

        Assert.notNull(tableName);
        Assert.notNull(idColumn);
        Assert.notNull(groupColumn);
        Assert.notNull(ordinalColumn);
    }

    /**
     * Устанавливает значение порядковой колонки.
     * Если новое значение порядковой колонки меньше нуля или больше максимального значения в группе,
     * то оно принимается равным максимальному значению в группе
     * Если группа записей пуста, значение колонки принимается равным 0.
     * @param template
     * @param id
     * @param newOrdinal новое значение порядковой колонки
     * @return значение порядковой колонки
     */
    public Integer updateOrdinal(SqlMapClientTemplate template, Integer id, Integer newOrdinal) {
        Assert.notNull(template);
        Assert.notNull(id);
        Assert.notNull(newOrdinal);

        Map<String, Object> paramMap = initParamMap(template, id);

        Integer lastOrdinal = (Integer) template.queryForObject("Ordinal.selectLastOrdinal", paramMap);
        Integer ordinal = (Integer) template.queryForObject("Ordinal.selectOrdinal", paramMap);
        if (ordinal != null) {
            // ordinal уже установлен. lastOrdinal не может быть равен null
            if (newOrdinal < 0 || newOrdinal > lastOrdinal) {
                newOrdinal = lastOrdinal;
            }
            if (ordinal.equals(newOrdinal)) {
                // нечего делать - уже верное значение
                return ordinal;
            } else {
                // обнулить ordinal
                paramMap.put("ordinal", null);
                template.update("Ordinal.updateOrdinal", paramMap);
            }
        } else {
            // ordinal еще не установлен
            if (lastOrdinal == null) {
                // пустая группа
                ordinal = 0;
                newOrdinal = 0;
            } else {
                ordinal = lastOrdinal + 1;
                if (newOrdinal < 0 || newOrdinal > lastOrdinal + 1) {
                    newOrdinal = lastOrdinal + 1;
                }
            }
        }

        if (newOrdinal > ordinal) {
            // перемещаем вниз
            // уменьшить все на единицу, начиная со старого номера и заканчивая новым
            paramMap.put("ordinal", ordinal);
            paramMap.put("maxOrdinal", newOrdinal);
            paramMap.put("value", -1);
            template.update("Ordinal.addOrdinal", paramMap);
        } else if (newOrdinal < ordinal) {
            // перемещаем вверх
            // увеличить все на единицу, начиная с нового номера и заканчивая старым
            paramMap.put("ordinal", newOrdinal);
            paramMap.put("maxOrdinal", ordinal);
            paramMap.put("value", +1);
            template.update("Ordinal.addOrdinal", paramMap);
        } else {
            // ordinal == newOrdinal - ничего не делать
        }
        paramMap.put("ordinal", newOrdinal);
        template.update("Ordinal.updateOrdinal", paramMap);
        return newOrdinal;
    }

    /**
     * Обнуляет порядковую колонку. Уменьшает значения порядковой колонки на единицу для
     * всех записей, значение порядковой колонки которых больше значения обнуляемой записи.
     * @param template
     * @param id
     */
    public void deleteOrdinal(SqlMapClientTemplate template, Integer id) {
        Assert.notNull(template);
        Assert.notNull(id);

        Map<String, Object> paramMap = initParamMap(template, id);

        Integer ordinal = (Integer) template.queryForObject("Ordinal.selectOrdinal", paramMap);
        Assert.notNull(ordinal);

        // обнулить ordinal
        paramMap.put("ordinal", null);
        template.update("Ordinal.updateOrdinal", paramMap);

        // уменьшить все на единицу
        paramMap.put("ordinal", ordinal);
        paramMap.put("value", -1);
        template.update("Ordinal.addOrdinal", paramMap);
    }

    private Map<String, Object> initParamMap(SqlMapClientTemplate template, Object id) {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("tableName", tableName);
        paramMap.put("idColumn", idColumn);
        paramMap.put("groupColumn", groupColumn);
        paramMap.put("ordinalColumn", ordinalColumn);

        paramMap.put("id", id);
        Object group = template.queryForObject("Ordinal.selectGroup", paramMap);
        paramMap.put("group", group);

        return paramMap;
    }
}
