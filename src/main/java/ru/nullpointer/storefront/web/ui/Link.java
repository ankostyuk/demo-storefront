package ru.nullpointer.storefront.web.ui;

import java.util.HashMap;
import java.util.Map;
import org.springframework.util.Assert;

/**
 * Класс работы с гиперссылками.
 * 
 * @author Alexander Yastrebov
 */
public class Link {

    private String url;
    private Map<String, Object> paramMap;

    /**
     * Конструктор без параметров
     * @param url
     */
    public Link(String url) {
        init(url, null);
    }

    /**
     * Конструктор с одним параметром
     * @param url
     * @param paramName
     * @param paramValue
     */
    public Link(String url, String paramName, Object paramValue) {
        Map<String, Object> m = new HashMap<String, Object>(1);
        m.put(paramName, paramValue);

        init(url, m);
    }

    /**
     * Конструктор с несколькими параметрами
     * @param url
     * @param paramMap
     */
    public Link(String url, Map<String, Object> paramMap) {
        init(url, paramMap);
    }

    private void init(String url, Map<String, Object> paramMap) {
        Assert.hasText(url);

        this.url = url;
        this.paramMap = paramMap;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }
}
