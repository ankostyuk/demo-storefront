package ru.nullpointer.storefront.web;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Alexander Yastrebov
 */
public class Metadata {

    private String title;
    private String description;
    private List<String> keywordList = new ArrayList<String>();
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public void addKeyword(String keyword) {
        keywordList.add(keyword);
    }

    public String getKeywords() {
        return StringUtils.join(keywordList, ", ");
    }
}
