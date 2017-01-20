package ru.nullpointer.storefront.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.CountryDAO;
import ru.nullpointer.storefront.domain.Country;

/**
 * 
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Service
public class CountryService {

    private Logger logger = LoggerFactory.getLogger(CountryService.class);
    //
    @Resource
    private CountryDAO countryDAO;

    /**
     * Возвращает страну по ISO 3166 alpha-2 коду
     * @param alpha2 ISO 3166 alpha-2 код страны
     * @return термин или <code>null</code> если термина с таким <code>alpha2</code> не существует
     * @throws IllegalArgumentException если <code>alpha2</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Country getCountryByAlpha2(String alpha2) {
        return countryDAO.getCountryByAlpha2(alpha2);
    }

    /**
     * Возвращает список стран.
     * Сортировка в алфавитном порядке.
     * @return
     */
    @Transactional(readOnly = true)
    public List<Country> getCountryList() {
        return countryDAO.getCountryList();
    }

    /**
     * Возвращает карту стран.
     * @param countryAlpha2Set набор <code>alpha2</code> - ISO 3166 alpha-2 кодов
     * @return Карту стран. Ключем является <code>alpha2</code> - ISO 3166 alpha-2 код. Карта может быть пустой если список <code>countryAlpha2Set</code> пуст или содержит неверные данные.
     * @throws IllegalArgumentException если <code>countryAlpha2Set</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Map<String, Country> getCountryMap(Set<String> countryAlpha2Set) {
        return countryDAO.getCountryMap(countryAlpha2Set);
    }

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
    @Transactional(readOnly = true)
    public List<Country> getCountryListByText(String text, int limit) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }

        return countryDAO.getCountryListByText(text, limit);
    }
}
