package ru.nullpointer.storefront.service;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nullpointer.storefront.dao.SessionDescriptorDAO;
import ru.nullpointer.storefront.domain.SessionDescriptor;

/**
 *
 * @author Alexander Yastrebov
 */
@Service
public class SessionService {

    private int sessionMaxAge = 7 * 24 * 60 * 60;
    private int sessionRefreshRate = 60 * 60;
    //
    @Resource
    private SessionDescriptorDAO sessionDescriptorDAO;
    @Resource
    private AnonymousSessionTracker anonymousSessionTracker;
    @Resource
    private TimeService timeService;

    /**
     * Возвращает идентификатор сессии пользователя или <code>null</code>
     * если сессии не существует.
     * Если <code>create</code> равен <code>true</code> и сессии не существует, 
     * предпринимается попытка создать сессию.
     * При успешном создании возвращается идентификатор созданной сессии.
     * @return
     */
    @Transactional
    public Integer getSessionId(boolean create) {
        String sid = anonymousSessionTracker.getSessionIdFromRequest();
        if (sid != null) {
            SessionDescriptor sd = sessionDescriptorDAO.getDescriptorBySessionId(sid);
            if (sd != null) {
                return sd.getId();
            } else if (create) {
                sd = new SessionDescriptor();
                sd.setSessionId(sid);
                sd.setTouchDate(timeService.now());
                sessionDescriptorDAO.insert(sd);
                return sd.getId();
            }
        }
        return null;
    }

    public int getSessionMaxAge() {
        return sessionMaxAge;
    }

    public int getSessionRefreshRate() {
        return sessionRefreshRate;
    }

    @Transactional
    void touchSession(String sid) {
        sessionDescriptorDAO.touch(sid, timeService.now());
    }
}
