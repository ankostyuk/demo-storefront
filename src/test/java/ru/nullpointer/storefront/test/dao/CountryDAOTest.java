package ru.nullpointer.storefront.test.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.CountryDAO;
import ru.nullpointer.storefront.domain.Country;

/**
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class CountryDAOTest {

    private static final int GET_COUNTRY_LIMIT = 100;

    @Resource
    private CountryDAO countryDAO;

    @Test
    public void test_read() {
        // READ ALL
        List<Country> countryList = countryDAO.getCountryList();
        assertNotNull(countryList);
        assertFalse(countryList.isEmpty());

        Set<String> countryAlpha2Set = new HashSet<String>();
        for (Country c : countryList) {
            countryAlpha2Set.add(c.getAlpha2());
        }
        Map<String, Country> countryMap = countryDAO.getCountryMap(countryAlpha2Set);

        assertEquals(countryList.size(), countryMap.size());

        Country country = null;
        for (Country c : countryList) {
            country = countryDAO.getCountryByAlpha2(c.getAlpha2());
            assertCountryEquals(c, country);

            country = countryMap.get(c.getAlpha2());
            assertNotNull(country);
            assertCountryEquals(c, country);
        }
    }

    @Test
    public void test_getCountryListByText() {
        Country country = countryDAO.getCountryByAlpha2("RU");
        assertNotNull(country);

        String text = null;
        List<Country> countryList = null;

        // alpha-2 code
        text = "rU";
        countryList = countryDAO.getCountryListByText(text, GET_COUNTRY_LIMIT);
        assertNotNull(countryList);
        assertFalse(countryList.isEmpty());

        // name
        text = "рОссИ";
        countryList = countryDAO.getCountryListByText(text, GET_COUNTRY_LIMIT);
        assertNotNull(countryList);
        assertEquals(1, countryList.size());
        assertCountryEquals(country, countryList.get(0));

        // keywords
        text = "рф";
        countryList = countryDAO.getCountryListByText(text, GET_COUNTRY_LIMIT);
        assertNotNull(countryList);
        assertFalse(countryList.isEmpty());

        text = "российска";
        countryList = countryDAO.getCountryListByText(text, GET_COUNTRY_LIMIT);
        assertNotNull(countryList);
        assertEquals(1, countryList.size());
        assertCountryEquals(country, countryList.get(0));

        text = "russi";
        countryList = countryDAO.getCountryListByText(text, GET_COUNTRY_LIMIT);
        assertNotNull(countryList);
        assertEquals(1, countryList.size());
        assertCountryEquals(country, countryList.get(0));

        // alpha-3 code
        text = "rUs";
        countryList = countryDAO.getCountryListByText(text, GET_COUNTRY_LIMIT);
        assertNotNull(countryList);
        assertFalse(countryList.isEmpty());

        // eng name
        text = "russian";
        countryList = countryDAO.getCountryListByText(text, GET_COUNTRY_LIMIT);
        assertNotNull(countryList);
        assertEquals(1, countryList.size());
        assertCountryEquals(country, countryList.get(0));

        text = "ussian fed";
        countryList = countryDAO.getCountryListByText(text, GET_COUNTRY_LIMIT);
        assertNotNull(countryList);
        assertEquals(1, countryList.size());
        assertCountryEquals(country, countryList.get(0));
    }

    private void assertCountryEquals(Country c1, Country c2) {
        assertEquals(c1.getAlpha2(), c2.getAlpha2());
        assertEquals(c1.getName(), c2.getName());
    }
}
