package ru.nullpointer.storefront.dao;

import java.util.Map;
import java.util.Set;
import ru.nullpointer.storefront.domain.param.ParamSetDescriptor;

/**
 *
 * @author ankostyuk
 */
public interface ParamSetDAO {

    /**
     * Возвращает дескриптор набора параметров по идентификатору
     * @param id идентификатор дескриптора набора параметров
     * @return дескриптор набора параметров или <code>null</code> если дескриптор набора параметров с таким <code>id</code> не существует
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    ParamSetDescriptor getParamSetDescriptorById(Integer id);

    /**
     * Добавляет дескриптор набора параметров. Устанавливает дескриптору набора параметров полученный <code>id</code>.
     * @param psd добавляемый дескриптор набора параметров
     * @throws IllegalArgumentException если <code>unit</code> равен <code>null</code> или обязательные атрибуты не "валидны" 
     */
    void addParamSetDescriptor(ParamSetDescriptor psd);

    /**
     * Удаляет дескриптор набора параметров.
     * @param id идентификатор дескриптора набора параметров
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     * @throws TODO Exception если нарушена связь со связанными данными
     */
    void deleteParamSetDescriptor(Integer id);

    /**
     * Возвращает набор параметров.
     * @param paramDescriptorId идентификатор дескриптора набора параметров
     * @param id
     * @return Карту значений параметров.
     * Ключем карты является <code>id</code> параметра.
     * Класс объекта-значения зависит от типа параметра.
     * @throws IllegalArgumentException если :
     * - любой из аргументов равен <code>null</code>
     * - дескриптор набора параметров с таким <code>paramDescriptorId</code> не существует
     * - набор параметров с таким <code>id</code> не существует
     */
    Map<Integer, Object> getParamSetById(Integer paramDescriptorId, Integer id);

    /**
     * Возвращает карту наборов параметров
     * @param paramSetDescriptorId идентификатор дескриптора набора параметров
     * @param paramSetIdSet набор идентификационных номеров наборов параметров
     * @return Карту наборов
     * Ключем карты является идентификационный номер набора параметров
     * Значением карта, где:
     * ключем является <code>id</code> параметра.
     * класс объекта-значения зависит от типа параметра.
     * @throws IllegalArgumentException если :
     * - любой из аргументов равен <code>null</code>
     * - дескриптор набора параметров с таким <code>paramDescriptorId</code> не существует
     * - набор идентификационных номеров <code>paramSetIdSet</code> пуст
     */
    Map<Integer, Map<Integer, Object>> getParamSetMap(Integer paramDescriptorId, Set<Integer> paramSetIdSet);

    /**
     * Добавляет набор параметров.
     * @param paramDescriptorId идентификатор дескриптора набора параметров
     * @param paramValueMap карта значений параметров.
     * Ключем карты является <code>id</code> параметра.
     * Класс объекта-значения зависит от типа параметра.
     * @return идентификатор созданного набора параметров
     * @throws IllegalArgumentException если:
     * - любой из аргументов равен <code>null</code>
     * - дескриптор набора параметров с таким <code>paramDescriptorId</code> не существует
     */
    Integer insert(Integer paramDescriptorId, Map<Integer, Object> paramValueMap);

    /**
     * Обновляет набор параметров.
     * @param paramDescriptorId идентификатор дескриптора набора параметров
     * @param paramValueMap карта значений параметров.
     * Ключем карты является <code>id</code> параметра.
     * Класс объекта-значения зависит от типа параметра.
     * @return идентификатор созданного набора параметров
     * @throws IllegalArgumentException если:
     * - любой из аргументов равен <code>null</code>
     * - дескриптор набора параметров с таким <code>paramDescriptorId</code> не существует
     * - набор параметров с таким <code>id</code> не существует
     */
    void update(Integer paramDescriptorId, Integer id, Map<Integer, Object> paramValueMap);

    /**
     * Удаляет набор параметров.
     * @param paramDescriptorId идентификатор дескриптора набора параметров
     * @param id идентификатор набора параметров
     * @throws IllegalArgumentException если:
     * - любой из аргументов равен <code>null</code>
     * - дескриптор набора параметров с таким <code>paramDescriptorId</code> не существует
     */
    void delete(Integer paramDescriptorId, Integer id);
}
