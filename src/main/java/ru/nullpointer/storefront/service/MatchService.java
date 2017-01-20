package ru.nullpointer.storefront.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.dao.CompanyDAO;
import ru.nullpointer.storefront.dao.MatchDAO;
import ru.nullpointer.storefront.dao.ModelDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.dao.ParamSetDAO;
import ru.nullpointer.storefront.dao.RegionDAO;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.ModelInfo;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.param.ParamSetDescriptor;
import ru.nullpointer.storefront.domain.support.AbstractMatch;
import ru.nullpointer.storefront.domain.support.MatchFilter;
import ru.nullpointer.storefront.domain.support.MatchSorting;
import ru.nullpointer.storefront.domain.support.ModelMatch;
import ru.nullpointer.storefront.domain.support.OfferMatch;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Service
public class MatchService {

    private Logger logger = LoggerFactory.getLogger(MatchService.class);
    //
    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private ParamSetDAO paramSetDAO;
    @Resource
    private MatchDAO matchDAO;
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private ModelDAO modelDAO;
    @Resource
    private CompanyDAO companyDAO;
    @Resource
    private RegionDAO regionDAO;

    /**
     * Возвращает список соответствий (моделей и предложений)
     * @param categoryId идентификационный номер категории
     * @param pageConfig настройки постраничного вывода
     * @param sorting параметр сортировки
     * @param region Регион. Может равняться <code>null</code>
     * @param filter фильтр по параметрам, может быть <code>null</code>
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedQueryResult<AbstractMatch> getCategoryMatchList(Integer categoryId, PageConfig pageConfig, MatchSorting sorting, Region region, MatchFilter filter) {
        Category category = categoryDAO.getCategoryById(categoryId);
        Assert.notNull(category);

        ParamSetDescriptor psd = null;
        if (category.isParametrized()) {
            psd = paramSetDAO.getParamSetDescriptorById(category.getParameterSetDescriptorId());
        }

        List<Match> matchList = matchDAO.getMatchList(categoryId, pageConfig, sorting, region, filter, psd);
        int matchCount = matchDAO.getMatchCount(categoryId, region, filter, psd);

        return new PaginatedQueryResult<AbstractMatch>(pageConfig, buildMatchResultList(matchList, region), matchCount);
    }

    /**
     * Возвращает количество соответствий (моделей и предложений)
     * @param categoryId идентификационный номер категории
     * @param region Регион. Может равняться <code>null</code>
     * @param filter фильтр по параметрам, может быть <code>null</code>
     * @return
     */
    @Transactional(readOnly = true)
    public int getCategoryMatchCount(Integer categoryId, Region region, MatchFilter filter) {
        Category category = categoryDAO.getCategoryById(categoryId);
        Assert.notNull(category);

        ParamSetDescriptor psd = null;
        if (category.isParametrized()) {
            psd = paramSetDAO.getParamSetDescriptorById(category.getParameterSetDescriptorId());
        }

        return matchDAO.getMatchCount(categoryId, region, filter, psd);
    }

    /**
     * Строит список результатов совпадений.
     * В списке будут содержаться только реально существующие совпадения (модели и предложения).
     * Если параметр <code>filter</code> не равен <code>null</code>, в списке
     * будут содержаться только отфильтрованные результаты.
     * Порядок в результирующем списке такой же как и во входном списоке дескрипторов совпадений.
     * @param matchList входной список дескрипторов совпадений
     * @param region регион или <code>null</code> для всех регионов
     * @param filter фильтр для фильтрации результатов, может быть <code>null</code>
     * @return
     * @throws IllegalArgumentException если matchList равен <code>null</code>
     * или у какого-либо дескриптора тип или ИД равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public List<AbstractMatch> buildMatchResultList(List<Match> matchList, Region region) {
        checkMatchList(matchList);

        if (matchList.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Integer> offerIdSet = getIdSet(matchList, Match.Type.OFFER);
        Set<Integer> modelIdSet = getIdSet(matchList, Match.Type.MODEL);

        Map<Integer, Offer> offerMap = offerDAO.getOfferMap(offerIdSet);
        Map<Integer, Model> modelMap = modelDAO.getModelMap(modelIdSet);
        Map<Integer, Company> companyMap = getCompanyMap(offerMap);
        Map<Integer, Region> companyRegionMap = getCompanyRegionMap(companyMap);
        Map<Integer, ModelInfo> modelInfoMap = matchDAO.getModelInfoMap(modelIdSet, region);

        List<AbstractMatch> resultList = new ArrayList<AbstractMatch>(matchList.size());
        for (Match m : matchList) {
            AbstractMatch matchResult = buildMatchResult(m, offerMap, modelMap, modelInfoMap, companyMap, companyRegionMap);
            if (matchResult != null) {
                resultList.add(matchResult);
            }
        }
        return resultList;
    }

    @Transactional(readOnly = true)
    public Map<Match, AbstractMatch> buildMatchResultMap(List<Match> matchList, Region region) {
        checkMatchList(matchList);

        if (matchList.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<Integer> offerIdSet = getIdSet(matchList, Match.Type.OFFER);
        Set<Integer> modelIdSet = getIdSet(matchList, Match.Type.MODEL);

        Map<Integer, Offer> offerMap = offerDAO.getOfferMap(offerIdSet);
        Map<Integer, Model> modelMap = modelDAO.getModelMap(modelIdSet);
        Map<Integer, Company> companyMap = getCompanyMap(offerMap);
        Map<Integer, Region> companyRegionMap = getCompanyRegionMap(companyMap);
        Map<Integer, ModelInfo> modelInfoMap = matchDAO.getModelInfoMap(modelIdSet, region);

        Map<Match, AbstractMatch> resultMap = new HashMap<Match, AbstractMatch>(matchList.size());
        for (Match m : matchList) {
            AbstractMatch matchResult = buildMatchResult(m, offerMap, modelMap, modelInfoMap, companyMap, companyRegionMap);
            if (matchResult != null) {
                resultMap.put(m, matchResult);
            }
        }
        return resultMap;
    }

    /**
     * Возвращает дополнительную информацию о модели.
     * @param modelId идентификатор модели
     * @param region регион, может быть <code>null</code>
     * @return Дополнительную информацию о модели или <code>null</code>,
     * если у модели нет доступных товарных предложений,
     * или модели с таким <code>modelId</code> не существует.
     */
    @Transactional(readOnly = true)
    public ModelInfo getModelInfo(Integer modelId, Region region) {
        Assert.notNull(modelId);

        Set<Integer> modelIdSet = new HashSet<Integer>();
        modelIdSet.add(modelId);

        Map<Integer, ModelInfo> modelInfoMap = matchDAO.getModelInfoMap(modelIdSet, region);

        return modelInfoMap.get(modelId);
    }

    /**
     * Возвращает список товарных предложений модели
     * @param modelId идентификатор модели
     * @param pageConfig настройки постраничного вывода
     * @param sorting параметр сортировки
     * @param region регион, может быть <code>null</code>
     * @param filter фильтр по <b>цене</b>, может быть <code>null</code>
     * @return
     */
    @Transactional(readOnly = true)
    public PaginatedQueryResult<OfferMatch> getModelOfferMatchList(Integer modelId, PageConfig pageConfig, MatchSorting sorting, Region region, MatchFilter filter) {
        Assert.notNull(modelId);

        List<Integer> offerIdList = matchDAO.getModelOfferIdList(modelId, pageConfig, sorting, region, filter);
        int total = matchDAO.getModelOfferCount(modelId, region, filter);

        if (offerIdList.isEmpty()) {
            return new PaginatedQueryResult<OfferMatch>(pageConfig, Collections.<OfferMatch>emptyList(), total);
        }

        Map<Integer, Offer> offerMap = offerDAO.getOfferMap(new HashSet<Integer>(offerIdList));
        Map<Integer, Company> companyMap = getCompanyMap(offerMap);
        Map<Integer, Region> companyRegionMap = getCompanyRegionMap(companyMap);

        List<OfferMatch> offerList = new ArrayList<OfferMatch>(offerIdList.size());

        for (Integer id : offerIdList) {
            Offer offer = offerMap.get(id);
            Company company = companyMap.get(offer.getCompanyId());
            OfferMatch offerMatch = new OfferMatch(offer, company, companyRegionMap.get(company.getRegionId()));
            offerList.add(offerMatch);
        }

        return new PaginatedQueryResult<OfferMatch>(pageConfig, offerList, total);
    }

    /**
     * Возвращает количество товарных предложений модели
     * @param modelId идентификатор модели
     * @param pageConfig настройки постраничного вывода
     * @param sorting параметр сортировки
     * @param region регион, может быть <code>null</code>
     * @param filter фильтр по <b>цене</b>, может быть <code>null</code>
     * @return
     */
    @Transactional(readOnly = true)
    public int getModelOfferMatchCount(Integer modelId, Region region, MatchFilter filter) {
        return matchDAO.getModelOfferCount(modelId, region, filter);
    }

    /**
     * Возвращает доступное подмножество совпадений.
     * Совпадение товарного предложения является доступным если:
     *      - предложение существует в каталоге
     *      - предложение является активным и одобрено модератором
     *      - если <code>categoryId</code> не равен <code>null</code>, то
     *          предложение должно принадлежать заданной категории
     *      - если <code>parametrizedOnly<code> равен <code>true</code>, то
     *          предложение должно быть с параметрами или связанным с моделью
     * Совпадение модели является доступным если:
     *      - модель существует в каталоге
     *      - если <code>categoryId</code> не равен <code>null</code>, то
     *          модель должна принадлежать заданной категории
     * @param matchSet множество совпадений
     * @param categoryId категория совпадения или <code>null</code>
     * @param parametrizedOnly если <code>true</code> включить только параметризованные предложения
     *          или предложения связанные с моделью
     * @return
     * @throws IllegalArgumentException если <code>matchSet</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Set<Match> getAccessibleMatchSubset(Set<Match> matchSet, Integer categoryId, boolean parametrizedOnly) {
        return matchDAO.getAccessibleMatchSubset(matchSet, categoryId, parametrizedOnly);
    }

    private AbstractMatch buildMatchResult(Match m, Map<Integer, Offer> offerMap, Map<Integer, Model> modelMap,
            Map<Integer, ModelInfo> modelInfoMap, Map<Integer, Company> companyMap, Map<Integer, Region> companyRegionMap) {

        AbstractMatch result = null;
        switch (m.getType()) {
            case OFFER:
                Offer offer = offerMap.get(m.getId());
                if (offer != null) {
                    Company company = companyMap.get(offer.getCompanyId());
                    result = new OfferMatch(offer, company, companyRegionMap.get(company.getRegionId()));
                }
                break;
            case MODEL:
                Model model = modelMap.get(m.getId());
                if (model != null) {
                    ModelInfo modelInfo = modelInfoMap.get(m.getId());
                    result = new ModelMatch(model, modelInfo);
                }
                break;
        }

        return result;
    }

    private Set<Integer> getIdSet(List<Match> matchList, Match.Type type) {
        Set<Integer> idSet = new HashSet<Integer>();
        for (Match m : matchList) {
            if (m.getType() == type) {
                idSet.add(m.getId());
            }
        }
        return idSet;
    }

    private Map<Integer, Company> getCompanyMap(Map<Integer, Offer> offerMap) {
        Set<Integer> companyIdSet = new HashSet<Integer>(offerMap.size());
        for (Offer offer : offerMap.values()) {
            companyIdSet.add(offer.getCompanyId());
        }
        return companyDAO.getCompanyMap(companyIdSet);
    }

    private Map<Integer, Region> getCompanyRegionMap(Map<Integer, Company> companyMap) {
        Set<Integer> regionIdSet = new HashSet<Integer>(companyMap.size());
        for (Company company : companyMap.values()) {
            regionIdSet.add(company.getRegionId());
        }
        return regionDAO.getRegionMap(regionIdSet);
    }

    private void checkMatchList(List<Match> matchList) {
        Assert.notNull(matchList);
        for (Match m : matchList) {
            Assert.notNull(m);
            Assert.notNull(m.getId());
            Assert.notNull(m.getType());
            Assert.isTrue(m.getType() == Match.Type.OFFER || m.getType() == Match.Type.MODEL, "Неизвестный тип соответствия");
        }
    }
}
