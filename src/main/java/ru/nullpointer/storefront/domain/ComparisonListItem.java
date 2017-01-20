package ru.nullpointer.storefront.domain;

/**
 *
 * @author Alexander Yastrebov
 */
public class ComparisonListItem {

    private Integer sessionId;
    private Integer categoryId;
    private Match match;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
