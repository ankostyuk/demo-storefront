package ru.nullpointer.storefront.test.dao;

import java.util.Date;
import static org.junit.Assert.*;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.dao.AccountDAO;
import ru.nullpointer.storefront.dao.BrandDAO;
import ru.nullpointer.storefront.dao.CatalogItemDAO;
import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.dao.CountryDAO;
import ru.nullpointer.storefront.dao.ModelDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.dao.RegionDAO;
import ru.nullpointer.storefront.dao.SessionDescriptorDAO;
import ru.nullpointer.storefront.dao.UnitDAO;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Country;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.SessionDescriptor;
import ru.nullpointer.storefront.domain.Unit;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.service.OfferService;
import ru.nullpointer.storefront.util.RandomUtils;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Component
class DAOTestHelper {

    private static final Integer CATEGORY_OFFER_PAGE_SIZE = 100;
    //
    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private CatalogItemDAO catalogItemDAO;
    @Resource
    private UnitDAO unitDAO;
    @Resource
    private BrandDAO brandDAO;
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private ModelDAO modelDAO;
    @Resource
    private AccountDAO accountDAO;
    @Resource
    private CountryDAO countryDAO;
    @Resource
    private SessionDescriptorDAO sessionDescriptorDAO;
    @Resource
    private RegionDAO regionDAO;

    /**
     * Возвращает первую категорию в каталоге без параметров
     * @return
     */
    Category getPlainCategory() {
        List<CatalogItem> itemList = catalogItemDAO.getSubTree(null);
        for (CatalogItem item : itemList) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                Category category = categoryDAO.getCategoryById(item.getId());
                assertNotNull(category);
                if (!category.isParametrized()) {
                    return category;
                }
            }
        }
        fail();
        return null;
    }

    /**
     * Возвращает первую категорию в каталоге c параметрами
     * @return
     */
    Category getParametrizedCategory() {
        List<CatalogItem> itemList = catalogItemDAO.getSubTree(null);
        for (CatalogItem item : itemList) {
            if (item.getType() == CatalogItem.Type.CATEGORY) {
                Category category = categoryDAO.getCategoryById(item.getId());
                assertNotNull(category);
                if (category.isParametrized()) {
                    return category;
                }
            }
        }
        fail();
        return null;
    }

    /**
     * Создает единицу измерения
     * @return
     */
    Unit createUnit() {
        Unit unit = new Unit();
        long x = System.currentTimeMillis();
        unit.setName("test unit#" + x);
        unit.setAbbreviation("a#" + x);
        unitDAO.addUnit(unit);
        assertNotNull(unit.getId());

        return unit;
    }

    /**
     * Создает торговую марку
     * @return
     */
    Brand createBrand() {
        Brand brand = new Brand();
        brand.setName("test brand#" + System.currentTimeMillis());
        brandDAO.insert(brand);
        assertNotNull(brand.getId());

        return brand;
    }

    /**
     * Возвращает модель с доступными товарными предложениями
     * @return
     */
    Model getModelWithOffer() {
        Category category = getParametrizedCategory();

        PaginatedQueryResult<Offer> result = null;
        PageConfig pageConfig = new PageConfig(1, CATEGORY_OFFER_PAGE_SIZE);

        do {
            List<Offer> list = offerDAO.getCategoryOfferPaginatedList(category.getId(), pageConfig);
            int total = offerDAO.getCategoryOfferCount(category.getId());
            result = new PaginatedQueryResult<Offer>(pageConfig, list, total);

            for (Offer offer : result.getList()) {
                if (offer.isModelLinked() && OfferService.isOfferAccessible(offer)) {
                    return modelDAO.getModelById(offer.getModelId());
                }
            }

            pageConfig = new PageConfig(result.getPageNumber() + 1, result.getPageSize());
        } while (pageConfig.getPageNumber() <= result.getPageCount());

        return null;
    }

    /**
     * Возвращает доступное товарное предложение
     * @return
     */
    Offer getAccessibleOffer() {
        Category category = getPlainCategory();

        PaginatedQueryResult<Offer> result = null;
        PageConfig pageConfig = new PageConfig(1, CATEGORY_OFFER_PAGE_SIZE);

        do {
            List<Offer> list = offerDAO.getCategoryOfferPaginatedList(category.getId(), pageConfig);
            int total = offerDAO.getCategoryOfferCount(category.getId());
            result = new PaginatedQueryResult<Offer>(pageConfig, list, total);

            for (Offer offer : result.getList()) {
                if (OfferService.isOfferAccessible(offer)) {
                    return offer;
                }
            }

            pageConfig = new PageConfig(result.getPageNumber() + 1, result.getPageSize());
        } while (pageConfig.getPageNumber() <= result.getPageCount());

        return null;
    }

    /**
     * Возвращает существующий аккаунт поставщика
     * @return
     */
    Account getCompanyAccount() {
        Account account = accountDAO.getAccountByEmail("company1@example.com");
        assertNotNull(account);
        assertTrue(account.getType() == Account.Type.COMPANY);

        return account;
    }

    /**
     * Возвращает существующий аккаунт менеджера
     * @return
     */
    Account getManagerAccount() {
        Account account = accountDAO.getAccountByEmail("manager1@example.com");
        assertNotNull(account);
        assertTrue(account.getType() == Account.Type.MANAGER);

        return account;
    }

    /**
     * Возвращает существующую страну
     * @return
     */
    Country getCountry() {
        List<Country> countryList = countryDAO.getCountryList();
        assertNotNull(countryList);
        assertFalse(countryList.isEmpty());

        return countryList.get(0);
    }

    /**
     * Возвращает список всех категорий, отсортированный по <code>id</code>.
     * @return
     */
    List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    /**
     * Создает сессию пользователя
     * @return
     */
    SessionDescriptor createSession() {
        String sessionId = RandomUtils.generateRandomString(32,
                RandomUtils.DIGITS,
                RandomUtils.ASCII_LOWER,
                RandomUtils.ASCII_UPPER);

        SessionDescriptor sd = new SessionDescriptor();
        sd.setSessionId(sessionId);
        sd.setTouchDate(new Date());

        // CREATE
        sessionDescriptorDAO.insert(sd);
        return sd;
    }

    /*
     * Возвращает регион по наименованию.
     * Если существует несколько регионов с таким именем - возвращает один из них.
     * @param name наименование региона
     * @return регион или <code>null</code> если региона с таким именем не существует
     */
    Region getRegionByName(String name) {
        List<Region> list = regionDAO.getRegionsByNameText(name);
        assertFalse(list.isEmpty());
        return list.get(0);
    }
}
