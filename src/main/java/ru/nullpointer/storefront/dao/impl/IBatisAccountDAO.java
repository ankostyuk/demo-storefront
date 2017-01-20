package ru.nullpointer.storefront.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.AccountDAO;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import ru.nullpointer.storefront.domain.Account;
import ru.nullpointer.storefront.domain.Account.Type;
import ru.nullpointer.storefront.domain.Role;
import ru.nullpointer.storefront.domain.support.AccountSorting;

/**
 *
 * @author Alexander Yastrebov
 */
public class IBatisAccountDAO extends SqlMapClientDaoSupport implements AccountDAO {

    @Override
    public boolean isEmailAvailable(String email) {
        Assert.hasText(email);
        return (Boolean) getSqlMapClientTemplate().queryForObject("Account.isEmailAvailable", email);
    }

    @Override
    public Account getAccountByEmail(String email) {
        Assert.hasText(email);
        return (Account) getSqlMapClientTemplate().queryForObject("Account.selectByEmail", email);
    }

    @Override
    public Account getAccountById(Integer id) {
        Assert.notNull(id);
        return (Account) getSqlMapClientTemplate().queryForObject("Account.selectById", id);
    }

    @Override
    public List<Account> getAccountListByType(Type type, AccountSorting sorting) {
        Assert.notNull(type);
        Assert.notNull(sorting);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("type", type);
        paramMap.put("sorting", sorting);

        return (List<Account>) getSqlMapClientTemplate().queryForList("Account.selectByType", paramMap);
    }

    @Override
    public Map<Integer, Account> getAccountMap(Set<Integer> accountIdSet) {
        Assert.notNull(accountIdSet);
        Assert.isTrue(!accountIdSet.isEmpty());

        return (Map<Integer, Account>) getSqlMapClientTemplate().queryForMap("Account.selectMapByIdList", new ArrayList<Integer>(accountIdSet), "id");
    }

    @Override
    public void insert(Account account) {
        Assert.notNull(account);
        getSqlMapClientTemplate().insert("Account.insert", account);
    }

    @Override
    public void update(Account account) {
        Assert.notNull(account);
        Assert.notNull(account.getId());
        getSqlMapClientTemplate().update("Account.update", account, 1);
    }

    @Override
    public void delete(Integer id) {
        Assert.notNull(id);
        getSqlMapClientTemplate().delete("Account.delete", id, 1);
    }

    @Override
    public List<Role> getRoleList(Integer accountId) {
        Assert.notNull(accountId);
        return (List<Role>) getSqlMapClientTemplate().queryForList("Account.selectRole", accountId);
    }

    @Override
    public void insertRole(Integer accountId, Role role) {
        Assert.notNull(accountId);
        Assert.notNull(role);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("accountId", accountId);
        paramMap.put("role", role);

        getSqlMapClientTemplate().insert("Account.insertRole", paramMap);
    }

    @Override
    public void deleteRole(Integer accountId, Role role) {
        Assert.notNull(accountId);
        Assert.notNull(role);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("accountId", accountId);
        paramMap.put("role", role);

        getSqlMapClientTemplate().delete("Account.deleteRole", paramMap, 1);
    }

    @Override
    public List<Integer> getAccountIdListFromRole(Role role) {
        Assert.notNull(role);

        return (List<Integer>) getSqlMapClientTemplate().queryForList("Account.getIdListByRole", role.name());
    }

    @Override
    public int getRegisteredAccountCount(Type type, Date startDate, Date endDate) {
        Assert.notNull(type);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("type", type);
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", endDate);

        return (Integer) getSqlMapClientTemplate().queryForObject("Account.countRegistered", paramMap);
    }
}
