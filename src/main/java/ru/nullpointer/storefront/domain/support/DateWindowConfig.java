package ru.nullpointer.storefront.domain.support;

import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
public class DateWindowConfig {

    private Date startDate;
    private Integer startId;
    private int limit;
    private boolean forward;
    
    public DateWindowConfig(Date startDate, Integer startId, int limit, boolean forward) {
        Assert.isTrue(limit > 0);

        this.startDate = startDate;
        this.startId = startId;
        this.limit = limit;
        this.forward = forward;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Integer getStartId() {
        return startId;
    }

    public int getLimit() {
        return limit;
    }

    public boolean isForward() {
        return forward;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("startDate", startDate)//
                .append("startId", startId)//
                .append("limit", limit)//
                .append("forward", forward)//
                .toString();
    }
}
