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
import ru.nullpointer.storefront.dao.BrandDAO;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.Category;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class BrandDAOTest {

    private static final int FIND_BRAND_LIMIT = 20;

    @Resource
    private BrandDAO brandDAO;
    @Resource
    private DAOTestHelper DAOTestHelper;

    @Test
    public void test_CRUD() {

        // READ ALL
        List<Brand> brandList = brandDAO.getBrandList();
        assertNotNull(brandList);
        assertFalse(brandList.isEmpty());

        Set<Integer> brandIdSet = new HashSet<Integer>();
        for (Brand b : brandList) {
            brandIdSet.add(b.getId());
        }
        Map<Integer, Brand> brandMap = brandDAO.getBrandMap(brandIdSet);

        Brand brand = null;
        for (Brand b : brandList) {
            brand = brandDAO.getBrandById(b.getId());
            assertBrandEquals(b, brand);

            brand = brandMap.get(b.getId());
            assertNotNull(brand);
            assertBrandEquals(b, brand);
        }

        brand = new Brand();

        brand.setName("test brand");
        brand.setKeywords("kw1 kw2 kw3");
        brand.setSite("test site");
        brand.setLogo("test logo");

        // CREATE
        brandDAO.insert(brand);
        Integer brandId = brand.getId();
        assertNotNull(brandId);

        // READ
        Brand brand2 = brandDAO.getBrandById(brandId);
        assertNotNull(brand2);
        assertBrandEquals(brand, brand2);

        // UPDATE
        brand.setName(brand.getName() + "UPD");
        brand.setSite(brand.getSite() + "UPD");
        brand.setKeywords(brand.getKeywords() + "UPD");
        brand.setLogo(brand.getLogo() + "UPD");

        brandDAO.updateInfo(brand);
        brand2 = brandDAO.getBrandById(brandId);
        assertBrandEquals(brand, brand2);

        // DELETE
        brandDAO.delete(brandId);
        brand2 = brandDAO.getBrandById(brandId);
        assertNull(brand2);
    }

    @Test
    public void test_getBrandNamePrefixList() {
        List<String> prefixList = brandDAO.getBrandNamePrefixList();
        assertNotNull(prefixList);
        assertFalse(prefixList.isEmpty());
        assertFalse(prefixList.contains(null));

        Set<String> s = new HashSet<String>(prefixList);
        assertEquals(prefixList.size(), s.size());
    }

    @Test
    public void test_getBrandListByIdSet() {
        List<Brand> brandAllList = brandDAO.getBrandList();

        Set<Integer> brandAllIdSet = new HashSet<Integer>();

        List<Brand> brandList = brandDAO.getBrandListByIdSet(brandAllIdSet);
        assertNotNull(brandList);
        assertTrue(brandList.isEmpty());

        //
        for (Brand brand : brandAllList) {
            brandAllIdSet.add(brand.getId());
        }

        brandList = brandDAO.getBrandListByIdSet(brandAllIdSet);

        assertUniqueBrandList(brandList);

        assertEquals(brandAllList.size(), brandList.size());

        //
        List<Brand> brandPartList = brandAllList.subList(0, brandAllList.size()/2);

        Set<Integer> brandPartIdSet = new HashSet<Integer>();

        for (Brand brand : brandPartList) {
            brandPartIdSet.add(brand.getId());
        }

        brandList = brandDAO.getBrandListByIdSet(brandPartIdSet);

        assertUniqueBrandList(brandList);

        assertEquals(brandPartList.size(), brandList.size());
    }

    @Test
    public void test_getCategoryBrandList() {
        Category cat = DAOTestHelper.getParametrizedCategory();

        List<Brand> brandList = brandDAO.getCategoryBrandList(cat.getId());
        
        assertUniqueBrandList(brandList);
    }

    @Test
    public void test_getIntersectionList() {
        List<Brand> brandAllList = brandDAO.getBrandList();
        Category category = DAOTestHelper.getParametrizedCategory();

        Set<Integer> brandAllIdSet = new HashSet<Integer>();
        Set<Integer> categoryIdSet = new HashSet<Integer>();

        List<Brand> brandList = brandDAO.getIntersectionList(brandAllIdSet, categoryIdSet);
        assertNotNull(brandList);
        assertTrue(brandList.isEmpty());

        //
        for (Brand brand : brandAllList) {
            brandAllIdSet.add(brand.getId());
        }

        categoryIdSet.add(category.getId());

        brandList = brandDAO.getIntersectionList(brandAllIdSet, categoryIdSet);

        assertUniqueBrandList(brandList);

        //
        List<Category> categoryAllList = DAOTestHelper.getAllCategories();

        for (Category c : categoryAllList) {
            categoryIdSet.add(c.getId());
        }

        brandList = brandDAO.getIntersectionList(brandAllIdSet, categoryIdSet);

        assertUniqueBrandList(brandList);
        assertTrue(brandAllIdSet.size() >= brandList.size());

        for (Brand brand : brandList) {
            assertTrue(brandAllIdSet.contains(brand.getId()));
        }
    }

    @Test
    public void test_findBrandListByText() {
        Brand brand = new Brand();
        brand.setName("test_find name");
        brand.setKeywords("test_find ключевое слово1 КЛЮЧЕВОЕ СЛОВО №2");
        brandDAO.insert(brand);

        List<Brand> brandList = brandDAO.findBrandListByText("test_find", FIND_BRAND_LIMIT);
        assertNotNull(brandList);
        assertTrue(brandList.size() == 1);
        assertEquals(brand.getId(), brandList.get(0).getId());

        brandList = brandDAO.findBrandListByText("КлючЕвое СлоВо", FIND_BRAND_LIMIT);
        assertNotNull(brandList);
        assertTrue(brandList.size() == 1);
        assertEquals(brand.getId(), brandList.get(0).getId());

        brandList = brandDAO.findBrandListByText("Ключевое слово №2", FIND_BRAND_LIMIT);
        assertNotNull(brandList);
        assertTrue(brandList.size() == 1);
        assertEquals(brand.getId(), brandList.get(0).getId());
    }

    @Test
    public void test_getBrandCount() {
        int count = brandDAO.getBrandCount();
        assertTrue(count > 0);
    }

    private void assertUniqueBrandList(List<Brand> brandList) {
        assertNotNull(brandList);
        assertFalse(brandList.isEmpty());
        
        Set<Integer> brandIdSet = new HashSet<Integer>();
        for (Brand brand : brandList) {
            assertNotNull(brand);
            assertFalse(brandIdSet.contains(brand.getId()));
            brandIdSet.add(brand.getId());
        }
    }

    private void assertBrandEquals(Brand b1, Brand b2) {
        assertEquals(b1.getId(), b2.getId());
        assertEquals(b1.getName(), b2.getName());
        assertEquals(b1.getKeywords(), b2.getKeywords());
        assertEquals(b1.getSite(), b2.getSite());
        assertEquals(b1.getLogo(), b2.getLogo());
    }
}
