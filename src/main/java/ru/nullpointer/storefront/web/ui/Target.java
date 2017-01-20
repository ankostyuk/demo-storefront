package ru.nullpointer.storefront.web.ui;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;

/**
 * @author ankostyuk
 */
@JsonWriteNullProperties(value = false)
public class Target {

    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)//
                .append("name", name)//
                .append("url", url)//
                .toString();
    }
}
