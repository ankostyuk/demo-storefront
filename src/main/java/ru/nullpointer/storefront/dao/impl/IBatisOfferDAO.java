package ru.nullpointer.storefront.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.support.CompanyOfferShowing;
import ru.nullpointer.storefront.domain.support.CompanyOfferSorting;
import ru.nullpointer.storefront.domain.support.DateWindowConfig;
import ru.nullpointer.storefront.domain.support.NumberInterval;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.StringCount;

/**
 *
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class IBatisOfferDAO extends SqlMapClientDaoSupport implements OfferDAO {

    @Override
    public int getCategoryOfferCount(Integer categoryId) {
        Assert.notNull(categoryId);
        return queryForSize("Offer.countByCategoryId", categoryId);
    }

    @Override
    public int getCategoryAccessibleOfferCount(Integer categoryId) {
        Assert.notNull(categoryId);
        return queryForSize("Offer.countAccessibleByCategoryId", categoryId);
    }

    @Override
    public int getCategoryAccessibleModelOfferCount(Integer categoryId) {
        Assert.notNull(categoryId);
        return queryForSize("Offer.countAccessibleModelByCategoryId", categoryId);
    }

    @Override
    public List<Offer> getCategoryOfferPaginatedList(Integer categoryId, PageConfig pageConfig) {
        Assert.notNull(categoryId);
        Assert.notNull(pageConfig);

        Map<String, Object> parameterMap = new HashMap<String, Object>();

        parameterMap.put("categoryId", categoryId);
        parameterMap.put("offset", pageConfig.getPageSize() * (pageConfig.getPageNumber() - 1));
        parameterMap.put("limit", pageConfig.getPageSize());

        return queryForList("Offer.selectPaginatedByCategoryId", parameterMap);
    }

    @Override
    public NumberInterval getCatalogOfferPriceInterval() {
        Map<String, BigDecimal> result = (Map<String, BigDecimal>) getSqlMapClientTemplate().queryForObject("Offer.selectPriceInterval");

        return new NumberInterval(result.get("min"), result.get("max"));
    }

    @Override
    public NumberInterval getCategoryOfferPriceInterval(Integer categoryId) {
        Assert.notNull(categoryId);

        Map<String, BigDecimal> result = (Map<String, BigDecimal>) getSqlMapClientTemplate().queryForObject("Offer.selectPriceIntervalByCategoryId", categoryId);

        return new NumberInterval(result.get("min"), result.get("max"));
    }

    @Override
    public NumberInterval getModelOfferPriceInterval(Integer modelId) {
        Assert.notNull(modelId);

        Map<String, BigDecimal> result = (Map<String, BigDecimal>) getSqlMapClientTemplate().queryForObject("Offer.selectPriceIntervalByModelId", modelId);

        return new NumberInterval(result.get("min"), result.get("max"));
    }

    @Override
    public List<Offer> getCompanyOfferList(Integer companyId, Set<Integer> categoryIdSet, PageConfig pageConfig, CompanyOfferShowing showing, CompanyOfferSorting sorting) {
        Assert.notNull(companyId);
        Assert.notNull(pageConfig);
        Assert.notNull(showing);
        Assert.notNull(sorting);

        List<Integer> categoryIdList = null;
        if (categoryIdSet != null) {
            categoryIdList = new ArrayList<Integer>(categoryIdSet);
        }

        Map<String, Object> parameterMap = new HashMap<String, Object>();

        parameterMap.put("companyId", companyId);
        parameterMap.put("categoryIdList", categoryIdList);
        parameterMap.put("showing", showing);
        parameterMap.put("sorting", sorting);
        parameterMap.put("offset", pageConfig.getPageSize() * (pageConfig.getPageNumber() - 1));
        parameterMap.put("limit", pageConfig.getPageSize());

        return queryForList("Offer.selectByCompanyId", parameterMap);
    }

    @Override
    public int getCompanyOfferCount(Integer companyId, Set<Integer> categoryIdSet, CompanyOfferShowing showing) {
        Assert.notNull(companyId);
        Assert.notNull(showing);

        List<Integer> categoryIdList = null;
        if (categoryIdSet != null) {
            categoryIdList = new ArrayList<Integer>(categoryIdSet);
        }

        Map<String, Object> parameterMap = new HashMap<String, Object>();

        parameterMap.put("companyId", companyId);
        parameterMap.put("categoryIdList", categoryIdList);
        parameterMap.put("showing", showing);

        return queryForSize("Offer.countByCompanyId", parameterMap);
    }

    @Override
    public Map<Integer, Integer> getCompanyOfferCountMap(Integer companyId) {
        Assert.notNull(companyId);
        return (Map<Integer, Integer>) getSqlMapClientTemplate().queryForMap("Offer.countMapByCompanyId", companyId, "id", "count");
    }

    @Override
    public Map<Integer, Integer> getModeratorPendingOfferCountMap(Integer moderatorId) {
        Assert.notNull(moderatorId);
        return (Map<Integer, Integer>) getSqlMapClientTemplate().queryForMap("Offer.countPendingMapByModeratorId", moderatorId, "id", "count");
    }

    @Override
    public List<Offer> getModeratorOfferList(Integer moderatorId, Integer categoryId, DateWindowConfig windowConfig, boolean pendingOnly) {
        Assert.notNull(moderatorId);
        Assert.notNull(categoryId);
        Assert.notNull(windowConfig);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("moderatorId", moderatorId);
        paramMap.put("categoryId", categoryId);
        paramMap.put("windowConfig", windowConfig);
        paramMap.put("pendingOnly", pendingOnly);

        return (List<Offer>) getSqlMapClientTemplate().queryForList("Offer.selectByModeratorId", paramMap);
    }

    @Override
    public Offer getOfferById(Integer id) {
        Assert.notNull(id);
        return queryForObject("Offer.selectById", id);
    }

    @Override
    public Map<Integer, Offer> getOfferMap(Set<Integer> offerIdSet) {
        Assert.notNull(offerIdSet);
        if (offerIdSet.isEmpty()) {
            return Collections.emptyMap();
        }
        // iBatis не поддерживает Set в качестве параметра, поэтому преобразуем в список
        List<Integer> param = new ArrayList<Integer>(offerIdSet);
        return queryForMap("Offer.selectMapByIdSet", param, "id");
    }

    @Override
    public List<StringCount> getUnlinkedBrandNameList() {
        return (List<StringCount>) getSqlMapClientTemplate().queryForList("Offer.selectUnlinkedBrandNames");
    }

    @Override
    public int setBrandByName(String brandName, Integer brandId) {
        Assert.hasText(brandName);
        Assert.notNull(brandId);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("brandName", brandName);
        paramMap.put("brandId", brandId);

        return getSqlMapClientTemplate().update("Offer.setBrandByName", paramMap);
    }

    @Override
    public void insert(Offer offer) {
        Assert.notNull(offer);
        getSqlMapClientTemplate().insert("Offer.insert", offer);
    }

    @Override
    public void updateInfo(Offer offer) {
        Assert.notNull(offer);
        Assert.notNull(offer.getId());
        getSqlMapClientTemplate().update("Offer.updateInfo", offer);
    }

    @Override
    public void delete(Offer offer) {
        Assert.notNull(offer);
        Assert.notNull(offer.getId());
        getSqlMapClientTemplate().delete("Offer.delete", offer, 1);
    }

    @Override
    public int updateOffersUnitPrice(String defaultCurrency, String priceCurrency, Integer companyId, Integer offerId) {
        Assert.notNull(defaultCurrency);
        Assert.notNull(priceCurrency);

        Map<String, Object> parameterMap = new HashMap<String, Object>();

        parameterMap.put("defaultCurrency", defaultCurrency);
        parameterMap.put("priceCurrency", priceCurrency);
        parameterMap.put("companyId", companyId);
        parameterMap.put("offerId", offerId);

        return getSqlMapClientTemplate().update("Offer.updateOffersUnitPrice", parameterMap);
    }

    @Override
    public int removeParamSetRefs(Integer categoryId) {
        Assert.notNull(categoryId);
        return getSqlMapClientTemplate().update("Offer.removeParamSetRefs", categoryId);
    }

    @Override
    public int setModeratorRefs(Integer currentId, Set<Integer> idSet) {
        Assert.notNull(idSet);
        // один из параметров должен быть указан
        Assert.isTrue(currentId != null || !idSet.isEmpty());

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("currentId", currentId);
        if (!idSet.isEmpty()) {
            List<Integer> idList = new ArrayList<Integer>(idSet);
            paramMap.put("idList", idList);
            paramMap.put("idListSize", idList.size());
        }
        return getSqlMapClientTemplate().update("Offer.setModeratorRefs", paramMap);
    }

    @Override
    public int deactivateOffers(Date actualDate) {
        Assert.notNull(actualDate);
        return getSqlMapClientTemplate().update("Offer.deactivateByActualDate", actualDate);
    }

    private Integer queryForSize(String select, Object param) {
        return (Integer) getSqlMapClientTemplate().queryForObject(select, param);
    }

    private Offer queryForObject(String select, Object param) {
        return (Offer) getSqlMapClientTemplate().queryForObject(select, param);
    }

    private List<Offer> queryForList(String select, Object param) {
        return (List<Offer>) getSqlMapClientTemplate().queryForList(select, param);
    }

    private Map<Integer, Offer> queryForMap(String select, Object param, String property) {
        return (Map<Integer, Offer>) getSqlMapClientTemplate().queryForMap(select, param, property);
    }
}
