package ru.nullpointer.cdn.tags;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Alexander Yastrebov
 */
public interface Container {

    void setProperties(Map<String, String> properties);

    String buildUrl(String name, String key, HttpServletRequest request);
}
