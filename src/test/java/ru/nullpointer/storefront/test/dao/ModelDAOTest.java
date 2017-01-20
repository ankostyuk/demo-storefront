package ru.nullpointer.storefront.test.dao;

import java.util.Collections;
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
import ru.nullpointer.storefront.dao.ModelDAO;
import ru.nullpointer.storefront.dao.ParamSetDAO;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.test.AssertUtils;

/**
 *
 * @author Alexander Yastrebov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class ModelDAOTest {

    @Resource
    private DAOTestHelper DAOTestHelper;
    @Resource
    private ParamSetDAO paramSetDAO;
    @Resource
    private ModelDAO modelDAO;

    @Test
    public void test_CRUD() {
        Category cat = DAOTestHelper.getParametrizedCategory();

        Map<Integer, Object> paramValues = Collections.emptyMap();
        Integer paramSetId = paramSetDAO.insert(cat.getParameterSetDescriptorId(), paramValues);

        Model model = new Model();

        model.setCategoryId(cat.getId());
        model.setParamSetId(paramSetId);
        model.setBrandId(DAOTestHelper.createBrand().getId());
        model.setName("test model");
        model.setDescription("test description");
        model.setVendorCode("test vendor code");
        model.setKeywords("test keywords");
        model.setSite("www.example.com/test-model");
        model.setImage("test image");
        model.setParamDescription("test param description");

        // CREATE
        modelDAO.insert(model);
        Integer modelId = model.getId();
        assertNotNull(modelId);

        // READ
        Model m = modelDAO.getModelById(modelId);
        assertNotNull(m);
        assertModelEquals(model, m);

        Set<Integer> modelIdSet = new HashSet<Integer>();
        modelIdSet.add(modelId);

        Map<Integer, Model> modelMap = modelDAO.getModelMap(modelIdSet);
        assertNotNull(model);
        assertTrue(modelMap.size() == 1);
        m = modelMap.get(modelId);
        assertNotNull(m);
        assertModelEquals(model, m);

        // UPDATE
        model.setName(model.getName() + "UPD");
        model.setDescription(model.getDescription() + "UPD");
        model.setVendorCode(model.getVendorCode() + "UPD");
        model.setKeywords(model.getKeywords() + " new keyword");
        model.setSite("www.new-model-site.org");
        model.setImage(model.getImage() + "UPD");
        model.setParamDescription(model.getParamDescription() + "UPD");

        modelDAO.updateInfo(model);
        m = modelDAO.getModelById(modelId);
        assertModelEquals(model, m);

        // DELETE
        modelDAO.delete(modelId);
        m = modelDAO.getModelById(modelId);
        assertNull(m);
    }

    @Test
    public void test_getCategoryModelList() {
        Category cat = DAOTestHelper.getParametrizedCategory();

        PageConfig pageCongig = new PageConfig(1, 10);
        List<Model> list = modelDAO.getCategoryModelList(cat.getId(), pageCongig);

        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.size() <= pageCongig.getPageSize());
    }

    @Test
    public void test_getCategoryModelCount() {
        Category cat = DAOTestHelper.getParametrizedCategory();
        int total = modelDAO.getCategoryModelCount(cat.getId());
        assertTrue(total > 0);
    }

    @Test
    public void test_getCategoryModelCountMap() {
        Category cat = DAOTestHelper.getParametrizedCategory();
        Set<Integer> categoryIdSet = new HashSet<Integer>();
        categoryIdSet.add(cat.getId());

        Map<Integer, Integer> countMap = modelDAO.getCategoryModelCountMap(categoryIdSet);
        assertNotNull(countMap);
        assertTrue(countMap.size() > 0);

        Integer count = countMap.get(cat.getId());
        assertNotNull(count);

        int total = modelDAO.getCategoryModelCount(cat.getId());
        assertEquals(total, count.intValue());
    }

    @Test
    public void test_findModelListByText() {
        Category cat = DAOTestHelper.getParametrizedCategory();

        Map<Integer, Object> paramValues = Collections.emptyMap();
        Integer paramSetId = paramSetDAO.insert(cat.getParameterSetDescriptorId(), paramValues);

        Integer categoryId = cat.getId();
        Integer brandId = DAOTestHelper.createBrand().getId();

        Model model = new Model();

        model.setCategoryId(categoryId);
        model.setParamSetId(paramSetId);
        model.setBrandId(brandId);
        model.setName("тест имени");
        model.setDescription("тест описания");
        model.setVendorCode("тест код производителя");
        model.setKeywords("ключевое слово1 КЛЮЧЕВОЕ СЛОВО2");

        modelDAO.insert(model);

        List<Model> modelList = null;

        modelList = modelDAO.findModelListByText("тест имени", categoryId, brandId, 10);
        checkFoundModelList(modelList, model);

        modelList = modelDAO.findModelListByText("ключевое", categoryId, brandId, 10);
        checkFoundModelList(modelList, model);

        modelList = modelDAO.findModelListByText("слово1", categoryId, brandId, 10);
        checkFoundModelList(modelList, model);

        modelList = modelDAO.findModelListByText("КЛЮЧЕВОЕ", categoryId, brandId, 10);
        checkFoundModelList(modelList, model);

        modelList = modelDAO.findModelListByText("СЛОВО2", categoryId, brandId, 10);
        checkFoundModelList(modelList, model);
    }

    private void checkFoundModelList(List<Model> modelList, Model expected) {
        assertNotNull(modelList);
        assertEquals(1, modelList.size());
        assertModelEquals(expected, modelList.get(0));
    }

    private void assertModelEquals(Model m1, Model m2) {
        AssertUtils.assertPropertiesEquals(m1, m2,
                "id", "categoryId", "paramSetId", "brandId",
                "name", "description", "vendorCode",
                "keywords", "site", "image", "paramDescription");
    }
}
