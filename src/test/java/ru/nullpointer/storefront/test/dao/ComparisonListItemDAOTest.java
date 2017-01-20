package ru.nullpointer.storefront.test.dao;

import java.util.ArrayList;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.ComparisonListItemDAO;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.ComparisonListItem;
import ru.nullpointer.storefront.domain.Match;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class ComparisonListItemDAOTest {

    private Logger logger = LoggerFactory.getLogger(ComparisonListItemDAOTest.class);
    //
    @Resource
    private ComparisonListItemDAO cliDAO;
    @Resource
    private DAOTestHelper DAOTestHelper;

    @Test
    public void test_CRD() {
        Integer sid = DAOTestHelper.createSession().getId();
        Category cat = DAOTestHelper.getParametrizedCategory();

        List<ComparisonListItem> itemList = new ArrayList<ComparisonListItem>();
        for (int i = 0; i < 10; i++) {
            ComparisonListItem item = new ComparisonListItem();
            item.setSessionId(sid);
            item.setCategoryId(cat.getId());
            item.setMatch(new Match(i % 2 == 0 ? Match.Type.OFFER : Match.Type.MODEL, i + 1));
            itemList.add(item);
        }

        // CREATE
        for (ComparisonListItem item : itemList) {
            cliDAO.insert(item);
        }

        // READ
        Map<Integer, List<Match>> comparisonMap = cliDAO.getComparisonMap(sid);
        assertNotNull(comparisonMap);
        assertEquals(1, comparisonMap.size());

        List<Match> matchList = comparisonMap.get(cat.getId());
        assertNotNull(matchList);

        assertEquals(itemList.size(), matchList.size());
        for (int i = 0; i < itemList.size(); i++) {
            assertEquals(itemList.get(i).getMatch(), matchList.get(i));
        }

        // DELETE
        ComparisonListItem removed = itemList.remove(0);
        cliDAO.delete(removed);

        comparisonMap = cliDAO.getComparisonMap(sid);
        matchList = comparisonMap.get(cat.getId());
        assertNotNull(matchList);

        assertEquals(itemList.size(), matchList.size());
        for (int i = 0; i < itemList.size(); i++) {
            assertEquals(itemList.get(i).getMatch(), matchList.get(i));
        }

        // DELETE ALL
        cliDAO.deleteAll(sid, cat.getId());

        comparisonMap = cliDAO.getComparisonMap(sid);
        assertNotNull(comparisonMap);
        assertEquals(0, comparisonMap.size());
    }
}
