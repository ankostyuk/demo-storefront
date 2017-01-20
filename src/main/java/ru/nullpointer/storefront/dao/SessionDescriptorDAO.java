package ru.nullpointer.storefront.dao;

import java.util.Date;
import ru.nullpointer.storefront.domain.SessionDescriptor;

/**
 *
 * @author Alexander Yastrebov
 */
public interface SessionDescriptorDAO {

    SessionDescriptor getDescriptorBySessionId(String sessionId);

    void insert(SessionDescriptor descriptor);

    /**
     * Обновляет временную метку
     * @param sessionId
     * @param touchDate
     */
    void touch(String sessionId, Date touchDate);
}
