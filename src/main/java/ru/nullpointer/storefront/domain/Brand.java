package ru.nullpointer.storefront.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonGetter;
import org.codehaus.jackson.annotate.JsonMethod;
import ru.nullpointer.storefront.validation.Url;

/**
 *
 * @author Alexander Yastrebov
 */
@JsonAutoDetect(JsonMethod.NONE)
public class Brand {

    private Integer id;
    //
    @NotNull
    @Size.List({
        @Size(min = 2, message = "{constraint.size.min}"),
        @Size(max = 63, message = "{constraint.size.max}")
    })
    private String name;
    //
    @Size(max = 255, message = "{constraint.size.max}")
    private String keywords;
    //
    @Size(max = 255, message = "{constraint.size.max}")
    @Url(message = "{constraint.url}")
    private String site;
    //
    private String logo;

    @JsonGetter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonGetter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
