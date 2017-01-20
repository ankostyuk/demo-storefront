package ru.nullpointer.cdn.tags;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Simple implementation of container.
 * 
 * @author Alexander Yastrebov
 */
public class SimpleContainer extends ContainerSupport {

    private boolean context = false;
    private String prefix = null;
    private String suffix = null;

    @Override
    public String buildUrl(String name, String key, HttpServletRequest request) {
        Validate.notEmpty(name);
        Validate.notEmpty(key);

        key = key.trim();
        if (key.startsWith("/")) {
            key = key.substring("/".length());
        }

        StringBuilder sb = new StringBuilder();
        if (context) {
            sb.append(request.getContextPath());
            sb.append("/");
        }

        if (prefix != null && !prefix.isEmpty()) {
            sb.append(prefix);
        }

        sb.append(key);

        if (suffix != null && !suffix.isEmpty()) {
            sb.append(suffix);
        }

        return sb.toString();
    }

    @Override
    protected void afterPropertiesSet() {
        context = getBoolean("context", false);
        prefix = getTrimmed("prefix");
        suffix = getTrimmed("suffix");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("context", context)//
                .append("prefix", prefix)//
                .append("suffix", suffix)//
                .toString();
    }
}
