package ru.nullpointer.storefront.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.dao.ModelDAO;
import ru.nullpointer.storefront.dao.ParamSetDAO;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.support.Constants;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.service.search.SearchIndexing;
import ru.nullpointer.storefront.service.support.FieldUtils;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Service
public class ModelService {

    private Logger logger = LoggerFactory.getLogger(ModelService.class);
    //
    @Resource
    private ModelDAO modelDAO;
    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private ParamSetDAO paramSetDAO;
    @Resource
    private ParamModelHelper paramModelHelper;

    /**
     * Возвращает модель по идентификационному номеру
     * @param modelId
     * @return
     */
    @Transactional(readOnly = true)
    public Model getModelById(Integer modelId) {
        return modelDAO.getModelById(modelId);
    }

    /**
     * Возвращает карту моделей
     * @param modelIdSet
     * @return
     */
    @Transactional(readOnly = true)
    public Map<Integer, Model> getModelMap(Set<Integer> modelIdSet) {
        return modelDAO.getModelMap(modelIdSet);
    }

    /**
     * Выполняет поиск моделей по тексту
     * @param text
     * @param categoryId - ИД категории для поиска модели или <code>null</code> для поиска среди всех категорий
     * @param brandId - ИД бренда или <code>null</code> для поиска среди всех брендов
     * @param limit максимальное количество возвращаемых моделей
     * @return
     */
    @Transactional(readOnly = true)
    public List<Model> findModelListByText(String text, Integer categoryId, Integer brandId, int limit) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }

        // TODO: Подключить поиск
        return modelDAO.findModelListByText(text, categoryId, brandId, limit);
    }

    /**
     * Возвращает список моделей в категории
     * @param categoryId
     * @param pageConfig
     * @return
     */
    @Secured("ROLE_MANAGER_MODEL")
    @Transactional(readOnly = true)
    public PaginatedQueryResult<Model> getModelList(Integer categoryId, PageConfig pageConfig) {
        List<Model> list = modelDAO.getCategoryModelList(categoryId, pageConfig);
        int total = modelDAO.getCategoryModelCount(categoryId);

        return new PaginatedQueryResult<Model>(pageConfig, list, total);
    }

    /**
     * Возвращает карту количества моделей по категориям
     * Ключем карты является идентификационный номер категории,
     * значением - количество моделей в этой категории
     * @param categoryIdSet набор ИД категорий
     * @return
     * @throws IllegalArgumentException если <code>categoryIdSet</code> равен <code>null</code>
     */
    @Secured("ROLE_MANAGER_MODEL")
    @Transactional(readOnly = true)
    public Map<Integer, Integer> getModelCountMap(Set<Integer> categoryIdSet) {
        return modelDAO.getCategoryModelCountMap(categoryIdSet);
    }

    @Secured("ROLE_MANAGER_MODEL")
    @Transactional(readOnly = true)
    public boolean canDeleteModel(Model model) {
        // TODO
        return false;
    }

    /**
     * Сохраняет модель
     * @param model
     * @param paramValueMap
     */
    @Secured("ROLE_MANAGER_MODEL")
    @Transactional
    @SearchIndexing
    public void storeModel(Model model, Map<Integer, Object> paramValueMap) {
        checkModel(model);

        Assert.notNull(paramValueMap);

        Category category = categoryDAO.getCategoryById(model.getCategoryId());
        Assert.notNull(category);
        Assert.isTrue(category.isParametrized());

        Integer psdId = category.getParameterSetDescriptorId();
        Integer paramSetId = paramSetDAO.insert(psdId, paramValueMap);

        model.setParamSetId(paramSetId);

        model.setParamDescription(paramModelHelper.buildParamDescription(psdId, paramValueMap));

        modelDAO.insert(model);
    }

    /**
     * Обновляет модель
     * @param model
     * @param paramValueMap
     */
    @Secured("ROLE_MANAGER_MODEL")
    @Transactional
    @SearchIndexing
    public void updateModelInfo(Model model, Map<Integer, Object> paramValueMap) {
        checkModel(model);

        Assert.notNull(paramValueMap);

        Category category = categoryDAO.getCategoryById(model.getCategoryId());
        Assert.notNull(category);
        Assert.isTrue(category.isParametrized());

        Integer psdId = category.getParameterSetDescriptorId();
        paramSetDAO.update(psdId, model.getParamSetId(), paramValueMap);

        model.setParamDescription(paramModelHelper.buildParamDescription(psdId, paramValueMap));

        modelDAO.updateInfo(model);
    }

    /**
     * Удаляет модель.
     */
    @Secured("ROLE_MANAGER_MODEL")
    @Transactional
    @SearchIndexing
    public void deleteModel(Model model) {
        Assert.notNull(model);
        Assert.notNull(model.getId());
        Assert.notNull(model.getCategoryId());
        Assert.notNull(model.getParamSetId());

        Category category = categoryDAO.getCategoryById(model.getCategoryId());

        paramSetDAO.delete(category.getParameterSetDescriptorId(), model.getParamSetId());
        modelDAO.delete(model.getId());
    }

    private void checkModel(Model model) {
        Assert.notNull(model);
        Assert.notNull(model.getCategoryId());

        FieldUtils.nullifyIfEmpty(model, "description");
        FieldUtils.nullifyIfEmpty(model, "keywords");
        FieldUtils.nullifyIfEmpty(model, "site");
        FieldUtils.nullifyIfEmpty(model, "image");
    }
}
