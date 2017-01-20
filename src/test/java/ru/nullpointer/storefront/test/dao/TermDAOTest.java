package ru.nullpointer.storefront.test.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.*;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.TermDAO;
import ru.nullpointer.storefront.domain.Term;

/**
 * @author ankostyuk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ru/nullpointer/storefront/test/test-context.xml")
@TransactionConfiguration
@Transactional
public class TermDAOTest {

    @Resource
    private TermDAO termDAO;

    @Test
    public void test_CRUD() {

        // READ ALL
        List<Term> termList = termDAO.getTermList();
        assertNotNull(termList);
        assertFalse(termList.isEmpty());

        Term term = new Term();

        term.setName("test term");
        term.setDescription("Description of test term");
        term.setSource("source");

        // CREATE
        termDAO.insert(term);
        Integer termId = term.getId();
        assertNotNull(termId);

        // READ
        Term term2 = termDAO.getTermById(termId);
        assertNotNull(term2);
        assertTermEquals(term, term2);

        // UPDATE
        term.setName("UPDATE " + term.getName());
        term.setDescription("UPDATE " + term.getDescription());
        term.setSource(null);

        termDAO.updateInfo(term);
        term2 = termDAO.getTermById(termId);
        assertTermEquals(term, term2);

        // DELETE
        termDAO.delete(termId);
        term2 = termDAO.getTermById(termId);
        assertNull(term2);
    }

    @Test
    public void test_getTermCount() {
        int count = termDAO.getTermCount();
        assertTrue(count > 0);
    }

    @Test
    public void test_getTermNamePrefixList() {
        List<String> prefixList = termDAO.getTermNamePrefixList();
        assertNotNull(prefixList);
        assertFalse(prefixList.isEmpty());
        assertFalse(prefixList.contains(null));

        Set<String> s = new HashSet<String>(prefixList);
        assertEquals(prefixList.size(), s.size());
    }

    @Test
    public void test_getTermListByPrefix() {
        List<String> prefixList = termDAO.getTermNamePrefixList();
        assertNotNull(prefixList);
        assertFalse(prefixList.isEmpty());
        assertFalse(prefixList.contains(null));

        int count = 0;
        for (String prefix : prefixList) {
            List<Term> termList = termDAO.getTermListByPrefix(prefix);
            assertNotNull(termList);
            assertFalse(termList.isEmpty());

            for (Term term : termList) {
                assertEquals(0, prefix.compareToIgnoreCase(term.getName().substring(0, 1)));
            }
            
            count += termList.size();
        }

        List<Term> termList = termDAO.getTermList();
        assertNotNull(termList);
        assertFalse(termList.isEmpty());
        
        assertEquals(termList.size(), count);
    }

    @Test
    public void test_getTermListByDigitPrefix() {
        List<String> prefixList = termDAO.getTermNamePrefixList();
        assertNotNull(prefixList);
        assertFalse(prefixList.isEmpty());
        assertFalse(prefixList.contains(null));

        for (String prefix : prefixList) {
            if (Character.isDigit(prefix.charAt(0))) {
                List<Term> termList = termDAO.getTermListByDigitPrefix();
                assertNotNull(termList);
                assertFalse(termList.isEmpty());

                for (Term term : termList) {
                    assertTrue(Character.isDigit(term.getName().charAt(0)));
                }
            }
        }
    }

    private void assertTermEquals(Term t1, Term t2) {
        assertEquals(t1.getId(), t2.getId());
        assertEquals(t1.getName(), t2.getName());
        assertEquals(t1.getDescription(), t2.getDescription());
        assertEquals(t1.getSource(), t2.getSource());
    }
}
