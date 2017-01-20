package ru.nullpointer.storefront.dao.impl;

import java.util.List;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.TermDAO;
import ru.nullpointer.storefront.domain.Term;

/**
 * @author ankostyuk
 */
public class IBatisTermDAO extends SqlMapClientDaoSupport implements TermDAO {

    @Override
    public int getTermCount() {
        return (Integer) getSqlMapClientTemplate().queryForObject("Term.count", null);
    }

    @Override
    public Term getTermById(Integer id) {
        Assert.notNull(id);
        return (Term) getSqlMapClientTemplate().queryForObject("Term.selectById", id);
    }

    @Override
    public List<Term> getTermList() {
        return (List<Term>) getSqlMapClientTemplate().queryForList("Term.selectList");
    }

    @Override
    public List<String> getTermNamePrefixList() {
        return (List<String>) getSqlMapClientTemplate().queryForList("Term.selectPrefixList");
    }

    @Override
    public List<Term> getTermListByPrefix(String prefix) {
        Assert.notNull(prefix);
        return (List<Term>) getSqlMapClientTemplate().queryForList("Term.selectListByPrefix", prefix);
    }

    @Override
    public List<Term> getTermListByDigitPrefix() {
        return (List<Term>) getSqlMapClientTemplate().queryForList("Term.selectListByDigitPrefix");
    }

    @Override
    public void insert(Term term) {
        Assert.notNull(term);
        getSqlMapClientTemplate().insert("Term.insert", term);
    }

    @Override
    public void updateInfo(Term term) {
        Assert.notNull(term);
        Assert.notNull(term.getId());
        getSqlMapClientTemplate().update("Term.updateInfo", term, 1);
    }

    @Override
    public void delete(Integer id) {
        Assert.notNull(id);
        getSqlMapClientTemplate().delete("Term.delete", id, 1);
    }
}
