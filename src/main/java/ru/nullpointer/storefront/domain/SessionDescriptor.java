package ru.nullpointer.storefront.domain;

import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Alexander Yastrebov
 */
public class SessionDescriptor {

    private Integer id;
    private String sessionId;
    private Date touchDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getTouchDate() {
        return touchDate;
    }

    public void setTouchDate(Date touchDate) {
        this.touchDate = touchDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("id", id)//
                .append("sessionId", sessionId)//
                .append("touchDate", touchDate)//
                .toString();
    }
}
