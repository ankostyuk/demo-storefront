package ru.nullpointer.storefront.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nullpointer.storefront.domain.Country;

/**
 * @author ankostyuk
 */
public interface CountryDAO {

    /**
     * Возвращает страну по ISO 3166 alpha-2 коду
     * @param alpha2 ISO 3166 alpha-2 код страны
     * @return термин или <code>null</code> если термина с таким <code>alpha2</code> не существует
     * @throws IllegalArgumentException если <code>alpha2</code> равен <code>null</code>
     */
    Country getCountryByAlpha2(String alpha2);

    /**
     * Возвращает список стран.
     * Сортировка в алфавитном порядке.
     * @return
     */
    List<Country> getCountryList();

    /**
     * Возвращает карту стран.
     * @param countryAlpha2Set набор <code>alpha2</code> - ISO 3166 alpha-2 кодов
     * @return Карту стран. Ключем является <code>alpha2</code> - ISO 3166 alpha-2 код. Карта может быть пустой если список <code>countryAlpha2Set</code> пуст или содержит неверные данные.
     * @throws IllegalArgumentException если <code>countryAlpha2Set</code> равен <code>null</code>
     */
    Map<String, Country> getCountryMap(Set<String> countryAlpha2Set);

    /**
     * Возвращает список стран по вхождению текста в:
     * наименовании,
     * ключевых словах,
     * ISO 3166 alpha-2 коде,
     * ISO 3166 alpha-3 коде,
     * ISO 3166 английском наименовании.
     * Сортировка в алфавитном порядке.
     * @return
     */
    List<Country> getCountryListByText(String text, int limit);
}
