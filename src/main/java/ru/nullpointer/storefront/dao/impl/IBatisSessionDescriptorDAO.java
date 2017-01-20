package ru.nullpointer.storefront.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.dao.SessionDescriptorDAO;
import ru.nullpointer.storefront.domain.SessionDescriptor;

/**
 *
 * @author Alexander Yastrebov
 */
public class IBatisSessionDescriptorDAO extends SqlMapClientDaoSupport implements SessionDescriptorDAO {

    @Override
    public SessionDescriptor getDescriptorBySessionId(String sessionId) {
        Assert.hasText(sessionId);
        return (SessionDescriptor) getSqlMapClientTemplate().queryForObject("SessionDescriptor.selectBySessionId", sessionId);
    }

    @Override
    public void insert(SessionDescriptor descriptor) {
        Assert.notNull(descriptor);
        getSqlMapClientTemplate().insert("SessionDescriptor.insert", descriptor);
    }

    @Override
    public void touch(String sessionId, Date touchDate) {
        Assert.notNull(sessionId);
        Assert.notNull(touchDate);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sessionId", sessionId);
        paramMap.put("touchDate", touchDate);

        // Дескриптора с таким Ид может не существовать
        // Не проверять кол-во измененных строк
        getSqlMapClientTemplate().update("SessionDescriptor.touch", paramMap);
    }
}
