package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.MatchDAO;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.ModelInfo;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.param.Param;
import ru.nullpointer.storefront.domain.param.ParamSetDescriptor;
import ru.nullpointer.storefront.domain.support.NumberInterval;
import ru.nullpointer.storefront.domain.support.MatchFilter;
import ru.nullpointer.storefront.domain.support.MatchSorting;
import ru.nullpointer.storefront.domain.support.PageConfig;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
public class IBatisMatchDAO extends SqlMapClientDaoSupport implements MatchDAO {

    @Override
    public List<Match> getMatchList(Integer categoryId, PageConfig pageConfig, MatchSorting sorting, Region region, MatchFilter filter, ParamSetDescriptor psd) {
        Assert.notNull(categoryId);
        Assert.notNull(pageConfig);
        Assert.notNull(sorting);
        // region может быть null
        // filter может быть null
        // psd может быть null

        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("categoryId", categoryId);
        parameterMap.put("sorting", sorting);
        parameterMap.put("offset", pageConfig.getPageSize() * (pageConfig.getPageNumber() - 1));
        parameterMap.put("limit", pageConfig.getPageSize());
        parameterMap.put("region", region);

        initFilterParams(filter, psd, parameterMap);

        return getSqlMapClientTemplate().queryForList("Match.selectFiltered", parameterMap);
    }

    @Override
    public int getMatchCount(Integer categoryId, Region region, MatchFilter filter, ParamSetDescriptor psd) {
        Assert.notNull(categoryId);
        // region может быть null
        // filter может быть null
        // psd может быть null

        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("categoryId", categoryId);
        parameterMap.put("region", region);

        initFilterParams(filter, psd, parameterMap);

        return (Integer) getSqlMapClientTemplate().queryForObject("Match.countFiltered", parameterMap);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, ModelInfo> getModelInfoMap(Set<Integer> modelIdSet, Region region) {
        Assert.notNull(modelIdSet);
        // region может быть null

        if (modelIdSet.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> parameterMap = new HashMap<String, Object>();
        // iBatis не поддерживает Set в качестве параметра, поэтому преобразуем в список
        parameterMap.put("modelIdList", new ArrayList<Integer>(modelIdSet));
        parameterMap.put("region", region);

        return getSqlMapClientTemplate().queryForMap("Match.selectModelInfoMap", parameterMap, "modelId");
    }

    @Override
    public List<Integer> getModelOfferIdList(Integer modelId, PageConfig pageConfig, MatchSorting sorting, Region region, MatchFilter filter) {
        Assert.notNull(modelId);
        Assert.notNull(pageConfig);
        Assert.notNull(sorting);
        // region может быть null
        // filter может быть null

        Map<String, Object> parameterMap = new HashMap<String, Object>();

        parameterMap.put("modelId", modelId);
        parameterMap.put("sorting", sorting);
        parameterMap.put("offset", pageConfig.getPageSize() * (pageConfig.getPageNumber() - 1));
        parameterMap.put("limit", pageConfig.getPageSize());
        parameterMap.put("region", region);

        if (filter != null) {
            NumberInterval price = filter.getPrice();
            if (price != null) {
                parameterMap.put("minPrice", price.getMin());
                parameterMap.put("maxPrice", price.getMax());
            }
        }

        return getSqlMapClientTemplate().queryForList("Match.selectModelOfferIdFiltered", parameterMap);
    }

    @Override
    public int getModelOfferCount(Integer modelId, Region region, MatchFilter filter) {
        Assert.notNull(modelId);
        // region может быть null
        // filter может быть null

        Map<String, Object> parameterMap = new HashMap<String, Object>();

        parameterMap.put("modelId", modelId);
        parameterMap.put("region", region);

        if (filter != null) {
            NumberInterval price = filter.getPrice();
            if (price != null) {
                parameterMap.put("minPrice", price.getMin());
                parameterMap.put("maxPrice", price.getMax());
            }
        }

        return (Integer) getSqlMapClientTemplate().queryForObject("Match.countModelOfferFiltered", parameterMap);
    }

    @Override
    public Set<Match> getAccessibleMatchSubset(Set<Match> matchSet, Integer categoryId, boolean parametrizedOnly) {
        Assert.notNull(matchSet);
        if (matchSet.isEmpty()) {
            return Collections.emptySet();
        }

        List<Match> modelMatchList = new ArrayList<Match>();
        List<Match> offerMatchList = new ArrayList<Match>();
        for (Match m : matchSet) {
            switch (m.getType()) {
                case OFFER:
                    offerMatchList.add(m);
                    break;
                case MODEL:
                    modelMatchList.add(m);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный тип совпадения: " + m.getType());
            }
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (!offerMatchList.isEmpty()) {
            paramMap.put("offerMatchList", offerMatchList);
        }
        if (!modelMatchList.isEmpty()) {
            paramMap.put("modelMatchList", modelMatchList);
        }
        paramMap.put("categoryId", categoryId);
        paramMap.put("parametrizedOnly", parametrizedOnly);

        List<Match> result = (List<Match>) getSqlMapClientTemplate().queryForList("Match.selectAccessibleMatches", paramMap);
        return new HashSet<Match>(result);
    }

    private void initFilterParams(MatchFilter filter, ParamSetDescriptor psd, Map<String, Object> paramMap) {
        if (psd != null) {
            // TODO: возможно требуется более надежная проверка
            // на наличие моделей в категории. Например count().
            // Или вынести как флаг - группировать по моделям или нет
            paramMap.put("filterModels", true);
        }

        if (filter != null) {
            NumberInterval price = filter.getPrice();
            if (price != null) {
                paramMap.put("minPrice", price.getMin());
                paramMap.put("maxPrice", price.getMax());
            }

            Set<Integer> brandIdSet = filter.getBrandIdSet();
            if (brandIdSet != null && !brandIdSet.isEmpty()) {
                paramMap.put("brandIdList", new ArrayList<Integer>(brandIdSet));
            }

            if (psd != null) {
                Map<Integer, Object> paramValueMap = filter.getParamValueMap();
                List<Param> fullParamList = (List<Param>) getSqlMapClientTemplate().queryForList("Param.selectByDescriptorId", psd.getId());
                List<ParamValueHolder> paramValueList = new ArrayList<ParamValueHolder>();
                for (Param p : fullParamList) {
                    Object value = paramValueMap.get(p.getId());
                    if (value != null) {
                        paramValueList.add(new ParamValueHolder(p, value));
                    }
                }
                if (!paramValueList.isEmpty()) {
                    paramMap.put("paramValueList", paramValueList);
                    paramMap.put("paramTableName", psd.getTableName());
                }
            }
        }
    }
}
