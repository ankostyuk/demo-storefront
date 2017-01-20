package ru.nullpointer.storefront.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.ComparisonListItemDAO;
import ru.nullpointer.storefront.domain.ComparisonListItem;
import ru.nullpointer.storefront.domain.Match;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Service
public class ComparisonService {

    public static final int MAX_COMPARISON_LIST_SIZE = 25;
    //
    @Resource
    private SessionService sessionService;
    @Resource
    private ComparisonListItemDAO cliDAO;
    @Resource
    private MatchService matchService;

    @Transactional
    public Map<Integer, List<Match>> getComparisonListMap() {
        Map<Integer, List<Match>> comparisonMap = Collections.emptyMap();

        Integer sessionId = sessionService.getSessionId(false);
        if (sessionId != null) {
            comparisonMap = cliDAO.getComparisonMap(sessionId);
        }

        if (!comparisonMap.isEmpty()) {
            filterAccessibleMatches(comparisonMap, sessionId);
        }
        return comparisonMap;
    }

    @Transactional
    public boolean addToComparison(Integer categoryId, Match match) {
        Assert.notNull(categoryId);
        Assert.notNull(match);

        Integer sessionId = sessionService.getSessionId(true);
        if (sessionId == null) {
            return false;
        }

        Map<Integer, List<Match>> comparisonListMap = cliDAO.getComparisonMap(sessionId);

        List<Match> matchList = comparisonListMap.get(categoryId);

        if (matchList != null && matchList.contains(match)) {
            return true;
        }

        if (matchList == null || (matchList.size() < MAX_COMPARISON_LIST_SIZE)) {
            ComparisonListItem item = new ComparisonListItem();
            item.setSessionId(sessionId);
            item.setCategoryId(categoryId);
            item.setMatch(match);
            cliDAO.insert(item);
            return true;
        }
        
        return false;
    }

    @Transactional
    public boolean deleteFromComparison(Integer categoryId, Match match) {
        Assert.notNull(categoryId);
        Assert.notNull(match);

        Integer sessionId = sessionService.getSessionId(true);
        if (sessionId == null) {
            return false;
        }

        ComparisonListItem item = new ComparisonListItem();
        item.setSessionId(sessionId);
        item.setCategoryId(categoryId);
        item.setMatch(match);

        cliDAO.delete(item);
        return true;
    }

    @Transactional
    public boolean deleteAllFromComparison(Integer categoryId) {
        Assert.notNull(categoryId);
        Integer sessionId = sessionService.getSessionId(true);
        if (sessionId == null) {
            return false;
        }

        cliDAO.deleteAll(sessionId, categoryId);
        return true;
    }

    private void filterAccessibleMatches(Map<Integer, List<Match>> comparisonMap, Integer sessionId) {
        // оставить только доступные совпадения
        // TODO: избавиться от цикла
        for (Integer categoryId : comparisonMap.keySet()) {
            List<Match> matchList = comparisonMap.get(categoryId);
            Set<Match> accessibleSet = matchService.getAccessibleMatchSubset(new HashSet<Match>(matchList), categoryId, true);
            Iterator<Match> it = matchList.iterator();
            while (it.hasNext()) {
                Match m = it.next();
                if (!accessibleSet.contains(m)) {
                    it.remove();

                    ComparisonListItem item = new ComparisonListItem();
                    item.setSessionId(sessionId);
                    item.setCategoryId(categoryId);
                    item.setMatch(m);
                    cliDAO.delete(item);
                }
            }
        }
    }
}
