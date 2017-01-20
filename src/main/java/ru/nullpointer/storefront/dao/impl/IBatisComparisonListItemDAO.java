package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.ComparisonListItemDAO;
import ru.nullpointer.storefront.domain.ComparisonListItem;
import ru.nullpointer.storefront.domain.Match;

/**
 *
 * @author Alexander Yastrebov
 */
public class IBatisComparisonListItemDAO extends SqlMapClientDaoSupport implements ComparisonListItemDAO {

    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, List<Match>> getComparisonMap(Integer sessionId) {
        Assert.notNull(sessionId);

        List<ComparisonListItem> itemList = getSqlMapClientTemplate().queryForList("ComparisonListItem.selectBySessionId", sessionId);
        if (itemList.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, List<Match>> result = new HashMap<Integer, List<Match>>();
        for (ComparisonListItem item : itemList) {
            Integer categoryId = item.getCategoryId();
            List<Match> matchList = result.get(categoryId);
            if (matchList == null) {
                matchList = new ArrayList<Match>();
            }
            matchList.add(item.getMatch());
            result.put(categoryId, matchList);
        }
        return result;
    }

    @Override
    public void insert(ComparisonListItem item) {
        Assert.notNull(item);
        getSqlMapClientTemplate().insert("ComparisonListItem.insert", item);
    }

    @Override
    public void delete(ComparisonListItem item) {
        Assert.notNull(item);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sessionId", item.getSessionId());
        paramMap.put("categoryId", item.getCategoryId());
        paramMap.put("match", item.getMatch());

        getSqlMapClientTemplate().delete("ComparisonListItem.delete", paramMap);
    }

    @Override
    public void deleteAll(Integer sessionId, Integer categoryId) {
        Assert.notNull(sessionId);
        Assert.notNull(categoryId);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sessionId", sessionId);
        paramMap.put("categoryId", categoryId);

        getSqlMapClientTemplate().delete("ComparisonListItem.delete", paramMap);
    }
}
